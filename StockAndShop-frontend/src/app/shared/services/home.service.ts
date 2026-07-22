import { computed, effect, inject, Injectable, linkedSignal, signal } from '@angular/core';
import { HttpClient, httpResource } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Home, HomeRequest } from '../../features/home/models/home.models';
import { Observable, skip, tap } from 'rxjs';
import { ShoppingList } from '../../features/shopping-list/models/shopping-list.models';
import { User, UserSearchResult } from '../models/user.models';
import { ProductStock, ProductStockDecrese } from '../models/productStock.models';
import { AuthService } from '../../features/auth/services/auth.service';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import {
  HomeExpense,
  HomeExpenseRequest,
  PagedHomeExpenseResponse,
} from '../../features/home/models/home-expense.model';

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

  readonly selectedHomeResource = httpResource<Home>(() =>
    this._selectedHomeId() ? `${this.apiUrl}/${this._selectedHomeId()}` : undefined,
  );

  readonly shoppingListsResource = httpResource<ShoppingList[]>(() =>
    this._selectedHomeId() ? `${this.apiUrl}/${this._selectedHomeId()}/shopping-list` : undefined,
  );

  readonly usersResource = httpResource<User[]>(() =>
    this._selectedHomeId() ? `${this.apiUrl}/${this._selectedHomeId()}/user` : undefined,
  );

  readonly stockResource = httpResource<ProductStock[]>(() =>
    this._selectedHomeId() ? `${this.apiUrl}/${this._selectedHomeId()}/stock` : undefined,
  );

  private readonly expensePageSize = 20;
  private _expensePage = signal(0);
  private _allExpenses = signal<HomeExpense[]>([]);

  readonly expenseResource = httpResource<PagedHomeExpenseResponse>(() =>
    // take the betginning of the apoi url and add -expense to get the right route
    this._selectedHomeId()
      ? `${this.apiUrl}-expense/${this._selectedHomeId()}?page=${this._expensePage()}&size=${this.expensePageSize}`
      : undefined,
  );

  readonly homes = computed(() => this.homesResource.value() ?? []);
  readonly selectedHome = computed(() => this.selectedHomeResource.value() ?? null);
  readonly shoppingLists = computed(() => this.shoppingListsResource.value() ?? []);
  readonly users = computed(() => this.usersResource.value() ?? []);
  readonly stock = computed(() => this.stockResource.value() ?? []);
  readonly expense = computed(() => this._allExpenses());
  readonly expenseHasMore = computed(() => this.expenseResource.value()?.hasMore ?? false);
  readonly expenseIsLoading = computed(() => this.expenseResource.isLoading());

  readonly isViewer = computed(() => {
    const email = this.authService.getUserEmail();
    return this.users().some((u) => u.email === email && u.homeRole === 'VIEWER');
  });

  constructor() {
    // skip(1) avoids triggering on the initial signal value emitted at subscription time
    toObservable(this.authService.authVersion)
      .pipe(skip(1), takeUntilDestroyed())
      .subscribe(() => {
        this._selectedHomeId.set(undefined);
        if (this.authService.isLoggedIn()) {
          this.homesResource.reload();
        }
      });

    // page 0 means a fresh load (new home selected, or reload after a mutation); any other page appends for "load more"
    effect(
      () => {
        if (this.expenseResource.isLoading()) return;
        const response = this.expenseResource.value();
        if (!response) return;
        if (this._expensePage() === 0) {
          this._allExpenses.set(response.expenses);
        } else {
          this._allExpenses.update((prev) => [...prev, ...response.expenses]);
        }
      },
      { allowSignalWrites: true },
    );
  }

  selectHome(id: string) {
    this._selectedHomeId.set(id);
    this._expensePage.set(0);
    this._allExpenses.set([]);
  }

  loadMoreExpenses() {
    if (!this.expenseHasMore() || this.expenseIsLoading()) return;
    this._expensePage.update((p) => p + 1);
  }

  createNewHome(data: HomeRequest) {
    this.http
      .post<Home>(`${this.apiUrl}`, data)
      .pipe(tap((home) => this.homesResource.update((homes) => [...(homes ?? []), home])))
      .subscribe();
  }

  deleteHome(id: string) {
    return this.http
      .delete(`${this.apiUrl}/${id}`)
      .pipe(
        tap(() =>
          this.homesResource.update((homes) => (homes ?? []).filter((home) => home.id !== id)),
        ),
      );
  }

  decreseStock(productStock: ProductStockDecrese) {
    return this.http.put(`${this.apiUrl}/${this._selectedHomeId()}/decrease-stock`, productStock);
  }

  searchUsers(query: string): Observable<UserSearchResult[]> {
    return this.http.get<UserSearchResult[]>(`${environment.apiUrl}/user/search`, {
      params: { query },
    });
  }

  addUser(email: string, role: 'USER' | 'VIEWER') {
    return this.http
      .post<User>(`${this.apiUrl}/${this._selectedHomeId()}/add-user`, { email, role })
      .pipe(tap(() => this.usersResource.reload()));
  }

  removeUser(userId: string) {
    return this.http
      .delete(`${this.apiUrl}/${this._selectedHomeId()}/delete-user`, { params: { userId } })
      .pipe(tap(() => this.usersResource.reload()));
  }

  addExpense(heRequest: HomeExpenseRequest) {
    return this.http.post(`${this.apiUrl}-expense/`, heRequest).pipe(
      tap(() => {
        // Reset to the first page so the new expense (most recent) shows up at the top
        if (this._expensePage() === 0) {
          this.expenseResource.reload();
        } else {
          this._expensePage.set(0);
        }
      }),
    );
  }

  refundUser(amount: number, payerId: string, receiverId: string) {
    return this.http
      .post(
        `${environment.apiUrl}/home-expense/${this._selectedHomeId()}/refund/${payerId}/${receiverId}`,
        null,
        { params: { amount } },
      )
      .pipe(tap(() => this.usersResource.reload()));
  }
}
