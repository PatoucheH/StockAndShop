import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient, httpResource } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { ProductItemRequest } from '../../shared/models/productItem.models';
import { tap } from 'rxjs';
import { ShoppingList, ShoppingListRequest } from './shopping-list.models';

@Injectable({
  providedIn: 'root',
})
export class ShoppingListService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/shopping-list`;

  private _selectedListId = signal<number | undefined>(undefined);

  readonly selectedShoppingListResource = httpResource<ShoppingList>(() =>
    this._selectedListId() !== undefined ? `${this.apiUrl}/${this._selectedListId()}` : undefined,
  );

  readonly selectedShoppingList = computed(
    () => this.selectedShoppingListResource.value() ?? null
  );
  readonly loading = this.selectedShoppingListResource.isLoading;

  private _favoritesResource = httpResource<ShoppingList[]>(() => `${this.apiUrl}/favorites`);

  readonly favoriteShoppingLists = computed(() => this._favoritesResource.value() ?? []);
  readonly isFavoritesLoading = computed(() => this._favoritesResource.isLoading());
  readonly hasFavoritesError = computed(() => !!this._favoritesResource.error());

  isFavorited(id: number): boolean {
    return this.favoriteShoppingLists().some((sl) => sl.id === id);
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
