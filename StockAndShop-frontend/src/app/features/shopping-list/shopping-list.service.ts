import { computed, inject, Injectable, linkedSignal } from '@angular/core';
import { HttpClient, httpResource } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { ProductItemRequest } from '../../shared/models/productItem.models';
import { skip, tap } from 'rxjs';
import { ShoppingList, ShoppingListRequest } from './shopping-list.models';
import { AuthService } from '../auth/auth.service';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';

@Injectable({
  providedIn: 'root',
})
export class ShoppingListService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/shopping-list`;
  private authService = inject(AuthService);

  private _selectedListId = linkedSignal<number | undefined>(() => {
    this.authService.authVersion();
    return undefined;
  });

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
  }

  selectShoppingList(id: number) {
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
