import { computed, inject, Injectable, linkedSignal } from '@angular/core';
import { HttpClient, httpResource } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { ProductItem, ProductItemRequest } from '../../shared/models/productItem.models';
import { EMPTY, skip, switchMap, tap } from 'rxjs';
import { ShoppingList, ShoppingListRequest } from './shopping-list.models';
import { AuthService } from '../auth/auth.service';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { WebSocketService } from '../../core/services/websocket.service';
import { ItemRemovedPayload, ItemToggledPayload, ShoppingListWsEvent } from '../../shared/models/websocket.models';

@Injectable({
  providedIn: 'root',
})
export class ShoppingListService {
  private apiUrl = `${environment.apiUrl}/shopping-list`;
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private wsService = inject(WebSocketService);

  // linkedSignal resets to undefined whenever authVersion changes, clearing list selection on logout/login
  private _selectedListId = linkedSignal<number | undefined>(() => {
    this.authService.authVersion();
    return undefined;
  });

  readonly allShoppingListUser = httpResource<ShoppingList[]>(() =>
    `${this.apiUrl}/all`
  );

  readonly selectedShoppingListResource = httpResource<ShoppingList>(() =>
    this._selectedListId() !== undefined ? `${this.apiUrl}/${this._selectedListId()}` : undefined,
  );

  readonly selectedShoppingList = computed(
    () => this.selectedShoppingListResource.value() ?? null
  );
  readonly loading = this.selectedShoppingListResource.isLoading;

  private _favoritesResource = httpResource<ShoppingList[]>(() => {
    this.authService.authVersion();
    return this.authService.isLoggedIn() ? `${this.apiUrl}/favorites` : undefined;
  });

  readonly favoriteShoppingLists = computed(() => this._favoritesResource.value() ?? []);
  readonly isFavoritesLoading = computed(() => this._favoritesResource.isLoading());
  readonly hasFavoritesError = computed(() => !!this._favoritesResource.error());

  isFavorited(id: number): boolean {
    return this.favoriteShoppingLists().some((sl) => sl.id === id);
  }

  constructor() {
    toObservable(this.authService.authVersion).pipe(skip(1), takeUntilDestroyed()).subscribe(() => {
      this._selectedListId.set(undefined);
      if (this.authService.isLoggedIn()) {
        this._favoritesResource.reload();
      }
    });
    toObservable(this._selectedListId).pipe(
      takeUntilDestroyed(),
      switchMap(id =>
        id !== undefined ? this.wsService.subscribe(`/topic/shopping-list/${id}`) : EMPTY
      )
    ).subscribe(msg => this.handleWsEvent(JSON.parse(msg.body) as ShoppingListWsEvent));
  }

  private handleWsEvent(event: ShoppingListWsEvent) {
    // Own events are ignored — local state was already updated optimistically when the action was dispatched
    if (event.triggeredByUsername === this.authService.getUserEmail()) return;
    switch (event.type) {
      case 'ITEM_TOGGLED': {
        const p = event.payload as ItemToggledPayload;
        this.updateItemCheckedState(p.itemId, p.isChecked);
        break;
      }
      case 'ITEM_ADDED':
        this.selectedShoppingListResource.update(list =>
          list ? { ...list, products: [...list.products, event.payload as ProductItem] } : list
        );
        break;
      case 'ITEMS_BATCH_ADDED':
        this.selectedShoppingListResource.update(list =>
          list ? { ...list, products: [...list.products, ...(event.payload as ProductItem[])] } : list
        );
        break;
      case 'ITEM_REMOVED': {
        const p = event.payload as ItemRemovedPayload;
        this.selectedShoppingListResource.update(list =>
          list ? { ...list, products: list.products.filter(item => item.id !== p.itemId) } : list
        );
        break;
      }
      case 'ITEM_TRANSFERRED':
        this.selectedShoppingListResource.reload();
        break;
    }
  }

  selectShoppingList(id: number) {
    // Selecting the same list forces a reload in case its content changed since last visit
    if (this._selectedListId() === id) {
      this.selectedShoppingListResource.reload();
    } else {
      this._selectedListId.set(id);
    }
  }

  updateItemCheckedState(itemId: number, isChecked: boolean) {
    this.selectedShoppingListResource.update(list => {
      if (!list) return list;
      return {
        ...list,
        products: list.products.map(p => p.id === itemId ? { ...p, isChecked } : p),
      };
    });
  }

  createShoppingList(shoppingList: ShoppingListRequest, homeId: string) {
    return this.http.post(`${this.apiUrl}/${homeId}`, shoppingList);
  }

  deleteShoppingList(id: number) {
    return this.http.delete(`${this.apiUrl}/${id}`).pipe(
      tap(() => {
        if (this._selectedListId() === id) {
          this._selectedListId.set(undefined);
        }
      }),
    );
  }

  addProductToShoppingList(product: ProductItemRequest, id: number) {
    return this.http
      .post<ShoppingList>(`${this.apiUrl}/${id}/add-product`, product)
      .pipe(tap((updatedList) => this.selectedShoppingListResource.update(() => updatedList)));
  }

  addListProductsToShoppingList(product : ProductItemRequest[], id: number) {
    return this.http
      .post<ShoppingList>(`${this.apiUrl}/${id}/add-list-products`, product)
      .pipe(tap((updatedList) => this.selectedShoppingListResource.update(() => updatedList)));
  }

  deleteProductFromShoppingList(productId: number) {
    return this.http
      .delete(`${this.apiUrl}/${this._selectedListId()}/remove-product/${productId}`)
      .pipe(tap(() => this.selectedShoppingListResource.update(list => {
        if (!list) return list;
        return { ...list, products: list.products.filter(p => p.id !== productId) };
      })));
  }

  addToStockItemCheckedofShoppingList(homeId: string) {
    return this.http.patch<ShoppingList>(
      `${this.apiUrl}/${this._selectedListId()}/home/${homeId}/checked-list`, {}
    ).pipe(tap((updatedList) => this.selectedShoppingListResource.update(() => updatedList)));
  }

  addToFavorite(id: number) {
    return this.http.post(`${this.apiUrl}/${id}/favorite`, {}).pipe(
      tap(() => this._favoritesResource.reload()),
    );
  }

  removeFromFavorite(id: number) {
    return this.http.delete(`${this.apiUrl}/${id}/favorite`).pipe(
      tap(() => this._favoritesResource.reload()),
    );
  }
}
