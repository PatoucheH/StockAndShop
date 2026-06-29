import { computed, inject, Injectable, linkedSignal } from '@angular/core';
import { HttpClient, httpResource } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Home, HomeRequest } from '../../features/home/models/home.models';
import { Observable, skip, tap } from 'rxjs';
import { ShoppingList } from '../../features/shopping-list/models/shopping-list.models';
import { User, UserSearchResult } from '../models/user.models';
import { ProductStock, ProductStockDecrese } from '../models/productStock.models';
import { AuthService } from '../../features/auth/services/auth.service';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';

@Injectable({
  providedIn: 'root',
})
export class HomeService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/home`;
  private authService = inject(AuthService);

  // linkedSignal resets to undefined whenever authVersion changes, clearing home selection on logout/login
  private _selectedHomeId = linkedSignal<string | undefined>(() => {
    this.authService.authVersion();
    return undefined;
  });

  readonly homesResource = httpResource<Home[]>(() => {
    this.authService.authVersion();
    return this.authService.isLoggedIn() ? `${this.apiUrl}` : undefined;
  });

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

  readonly isViewer = computed(() => {
    const email = this.authService.getUserEmail();
    return this.users().some(u => u.email === email && u.homeRole === 'VIEWER');
  });

  constructor() {
    // skip(1) avoids triggering on the initial signal value emitted at subscription time
    toObservable(this.authService.authVersion).pipe(skip(1), takeUntilDestroyed()).subscribe(() => {
      this._selectedHomeId.set(undefined);
      if (this.authService.isLoggedIn()) {
        this.homesResource.reload();
      }
    });
  }

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

  searchUsers(query: string): Observable<UserSearchResult[]> {
    return this.http.get<UserSearchResult[]>(`${environment.apiUrl}/user/search`, { params: { query } });
  }

  addUser(homeId: string, email: string, role: 'USER' | 'VIEWER') {
    return this.http.post<User>(`${this.apiUrl}/${homeId}/add-user`, { email, role })
      .pipe(tap(() => this.usersResource.reload()));
  }

  removeUser(homeId: string, userId: string) {
    return this.http.delete(`${this.apiUrl}/${homeId}/delete-user`, { params: { userId } })
      .pipe(tap(() => this.usersResource.reload()));
  }
}
