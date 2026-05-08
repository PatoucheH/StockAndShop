import { inject, Injectable, signal } from '@angular/core';
import { ProductItem, ProductItemRequest } from '../../shared/models/productItem.models';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { finalize, tap } from 'rxjs';
import { ShoppingList, ShoppingListRequest } from './shopping-list.models';

@Injectable({
  providedIn: 'root',
})
export class ShoppingListService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/shopping-list`;

  private _loading = signal<boolean>(false);
  private _selectedShoppingList = signal<ShoppingList | null>(null);

  public loading = this._loading.asReadonly();
  public selectedShoppingList = this._selectedShoppingList.asReadonly();

  // LOAD DATA

  loadSelectedShoppingList(id: number) {
    this._loading.set(true);
    this.http.get<ShoppingList>(`${this.apiUrl}/${id}`).pipe(
      tap((data) => this._selectedShoppingList.set(data)),
      finalize(() => this._loading.set(false)),
    ).subscribe();
  }

  // CREATE

  createShoppingList(shoppingList: ShoppingListRequest, homeId: string) {
    return this.http.post(`${this.apiUrl}/${homeId}`, shoppingList);
  }

  /// DELETE
  deleteShoppingList(id: number) {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  // ADD PRODUCT
  addProductToShoppingList(product: ProductItemRequest, id: number) {
    return this.http.post(`${this.apiUrl}/${id}/add`, product);
  }
}
