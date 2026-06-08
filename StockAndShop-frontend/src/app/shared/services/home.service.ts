import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient, httpResource } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Home, HomeRequest } from '../../features/home/home.model';
import { tap } from 'rxjs';
import { ShoppingList } from '../../features/shopping-list/shopping-list.models';
import { User } from '../models/user.models';
import { ProductStock, ProductStockDecrese } from '../models/productStock.models';

@Injectable({
  providedIn: 'root',
})
export class HomeService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/home`;

  private _selectedHomeId = signal<string | undefined>(undefined);
  readonly homesResource = httpResource<Home[]>(() => `${this.apiUrl}`);

  readonly selectedHomeResource = httpResource<Home>(
    () => this._selectedHomeId() ? `${this.apiUrl}/${this._selectedHomeId()}` : undefined,
  );

  readonly shoppingListsResource = httpResource<ShoppingList[]>(
    () =>
      this._selectedHomeId()
        ? `${this.apiUrl}/${this._selectedHomeId()}/shopping-list`
        : undefined,
  );

  readonly usersResource = httpResource<User[]>(
    () =>
      this._selectedHomeId() ? `${this.apiUrl}/${this._selectedHomeId()}/user` : undefined,
  );

  readonly stockResource = httpResource<ProductStock[]>(
    () =>
      this._selectedHomeId() ? `${this.apiUrl}/${this._selectedHomeId()}/stock` : undefined,
  );

  readonly homes = computed(() => this.homesResource.value() ?? []);
  readonly selectedHome = computed(() => this.selectedHomeResource.value() ?? null);
  readonly shoppingLists = computed(() => this.shoppingListsResource.value() ?? []);
  readonly users = computed(() => this.usersResource.value() ?? []);
  readonly stock = computed(() => this.stockResource.value() ?? []);

  selectHome(id: string) {
    this._selectedHomeId.set(id);
  }

  createNewHome(data: HomeRequest) {
    this.http
      .post<Home>(`${this.apiUrl}`, data)
      .pipe(tap((home) => this.homesResource.update((homes) => [...(homes ?? []), home])))
      .subscribe();
  }

  deleteHome(id: string) {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }

  decreseStock(productStock: ProductStockDecrese){
    return this.http.put(`${this.apiUrl}/${this._selectedHomeId()}/decrease-stock`, productStock);
  }

  addUser(){

  }
}
