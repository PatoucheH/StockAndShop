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

  readonly selectedShoppingListResource = httpResource<ShoppingList>(
    () =>
      this._selectedListId() !== undefined
        ? `${this.apiUrl}/${this._selectedListId()}`
        : undefined,
  );

  readonly selectedShoppingList = computed(
    () => this.selectedShoppingListResource.value() ?? null,
  );
  readonly loading = this.selectedShoppingListResource.isLoading;

  selectShoppingList(id: number) {
    this._selectedListId.set(id);
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
}
