import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Home, HomeRequest } from './home.model';
import { finalize, tap } from 'rxjs';
import { ShoppingList } from '../shopping-list/shopping-list.models';
import { User } from '../../shared/models/user.models';
import { ProductStock } from '../../shared/models/productStock.models';

@Injectable({
  providedIn: 'root',
})
export class HomeService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/home`;

  private _homes = signal<Home[]>([]);
  private _selectedHome = signal<Home | null>(null);
  private _loading = signal<boolean>(false);
  private _shoppingLists = signal<ShoppingList[]>([]);
  private _users = signal<User[]>([]);
  private _stock = signal<ProductStock[]>([]);

  public homes = this._homes.asReadonly();
  public selectedHome = this._selectedHome.asReadonly();
  public loading = this._loading.asReadonly();
  public shoppingLists = this._shoppingLists.asReadonly();
  public users = this._users.asReadonly();
  public stock = this._stock.asReadonly();

  // LOAD DATA

  loadHomes() {
    this._loading.set(true);
    this.http
      .get<Home[]>(`${this.apiUrl}`)
      .pipe(
        tap((data) => this._homes.set(data)),
        finalize(() => this._loading.set(false)),
      )
      .subscribe();
  }

  loadHomeById(id: string) {
    this._loading.set(true);
    this._selectedHome.set(null);
    this.http
      .get<Home>(`${this.apiUrl}/${id}`)
      .pipe(
        tap((data) => this._selectedHome.set(data)),
        finalize(() => this._loading.set(false)),
      )
      .subscribe();
  }

  loadShoppingListHomeById(id: string) {
    this._loading.set(true);
    this.http
      .get<ShoppingList[]>(`${this.apiUrl}/${id}/shopping-list`)
      .pipe(
        tap((data) => this._shoppingLists.set(data)),
        finalize(() => this._loading.set(false)),
      )
      .subscribe();
  }

  reloadShoppingList() {
    const home = this.selectedHome();
    if (!home) return;
    this.http
      .get<ShoppingList[]>(`${this.apiUrl}/${home.id}/shopping-list`)
      .pipe(tap((data) => this._shoppingLists.set(data)))
      .subscribe();
  }

  loadUserHomeById(id: string) {
    this._loading.set(true);
    this.http
      .get<User[]>(`${this.apiUrl}/${id}/user`)
      .pipe(
        tap((data) => this._users.set(data)),
        finalize(() => this._loading.set(false)),
      )
      .subscribe();
  }

  loadStockHomeById(id: string) {
    this._loading.set(true);
    this.http
      .get<ProductStock[]>(`${this.apiUrl}/${id}/stock`)
      .pipe(
        tap((data) => this._stock.set(data)),
        finalize(() => this._loading.set(false)),
      )
      .subscribe();
  }

  // CREATE
  createNewHome(data: HomeRequest) {
    this.http
      .post<Home>(`${this.apiUrl}`, data)
      .pipe(tap((home) => this._homes.update((homes) => [...homes, home])))
      .subscribe();
  }

  // DELETE
  deleteHome(id: string) {
    return this.http.delete(`${this.apiUrl}/${id}`);
  }
}
