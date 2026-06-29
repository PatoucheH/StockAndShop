import { computed, effect, inject, Injectable, signal } from '@angular/core';
import { HttpClient, httpResource } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { PagedRecipeResponse, Recipe } from '../../../shared/models/recipe.models';
import { skip, tap } from 'rxjs';
import { AuthService } from '../../auth/services/auth.service';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { ProductStock } from '../../../shared/models/productStock.models';

@Injectable({ providedIn: 'root' })
export class RecipeService {
  private apiUrl = `${environment.apiUrl}/recipe`;
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private readonly pageSize = 50;

  private _page = signal(0);
  private _allRecipes = signal<Recipe[]>([]);
  private _newRecipes = signal<Recipe[]>([]);

  private _resource = httpResource<PagedRecipeResponse>(() => {
    this.authService.authVersion();
    return this.authService.isLoggedIn()
      ? `${this.apiUrl}?page=${this._page()}&size=${this.pageSize}`
      : undefined;
  });

  constructor() {
    // allowSignalWrites: true is required because this effect writes to _allRecipes
    // page 0 means a fresh load (reset after auth change), any other page appends for infinite scroll
    effect(
      () => {
        if (this._resource.isLoading()) return;
        const response = this._resource.value();
        if (!response) return;
        if (this._page() === 0) {
          this._allRecipes.set(response.recipes);
        } else {
          this._allRecipes.update((prev) => [...prev, ...response.recipes]);
        }
      },
      { allowSignalWrites: true },
    );

    toObservable(this.authService.authVersion)
      .pipe(skip(1), takeUntilDestroyed())
      .subscribe(() => {
        if (this.authService.isLoggedIn()) {
          this._favoritesResource.reload();
        }
      });
  }

  private _favoritesResource = httpResource<Recipe[]>(() => {
    this.authService.authVersion();
    return this.authService.isLoggedIn() ? `${this.apiUrl}/favorites` : undefined;
  });

  readonly isLoading = computed(() => this._resource.isLoading());
  readonly hasError = computed(() => !!this._resource.error());
  readonly recipes = computed(() => this._allRecipes());
  readonly newRecipes = computed(() => this._newRecipes());
  readonly hasMore = computed(() => this._resource.value()?.hasMore ?? false);
  readonly isGeneratingRecipe = signal(false);

  readonly favoriteRecipes = computed(() => this._favoritesResource.value() ?? []);
  readonly isFavoritesLoading = computed(() => this._favoritesResource.isLoading());
  readonly hasFavoritesError = computed(() => !!this._favoritesResource.error());

  isFavorited(id: string): boolean {
    return this.favoriteRecipes().some((r) => r.id === id);
  }

  /** Increments the page signal, which triggers the httpResource to fetch the next batch for infinite scroll. */
  loadNextPage() {
    if (!this.hasMore() || this.isLoading()) return;
    this._page.update((p) => p + 1);
  }

  /**
   * Fetches recipe suggestions. When products are provided, POSTs their names so the backend
   * filters by that specific subset; otherwise GETs suggestions based on the home's full stock.
   */
  generateRecipe(homeId: string, products?: ProductStock[], onError?: (msg: string) => void) {
    this.isGeneratingRecipe.set(true);
    const request$ = products?.length
      ? this.http.post<Recipe[]>(`${this.apiUrl}/${homeId}/suggestions`, { productNames: products.map(p => p.nameProduct) })
      : this.http.get<Recipe[]>(`${this.apiUrl}/${homeId}/suggestions`);
    request$.subscribe({
      next: (recipes) => {
        this._newRecipes.set(recipes);
        this.isGeneratingRecipe.set(false);
      },
      error: (err) => {
        this.isGeneratingRecipe.set(false);
        onError?.(err?.error ?? 'Impossible de générer une recette.');
      },
    });
  }

  clearNewRecipes() {
    this._newRecipes.set([]);
  }

  /** Calls the AI-generate endpoint and prepends the new recipe to both the global list and the "new" buffer. */
  generateNewRecipe(homeId: string, onError?: (msg: string) => void) {
    this.isGeneratingRecipe.set(true);
    this.http.post<Recipe>(`${this.apiUrl}/${homeId}/generate`, {}).subscribe({
      next: (recipe) => {
        this._newRecipes.update((prev) => [recipe, ...prev]);
        this._allRecipes.update((prev) => [recipe, ...prev]);
        this.isGeneratingRecipe.set(false);
      },
      error: (err) => {
        this.isGeneratingRecipe.set(false);
        onError?.(err?.error ?? 'Impossible de générer une recette.');
      },
    });
  }

  addToFavoriteRecipe(recipeId: string) {
    return this.http
      .post(`${this.apiUrl}/${recipeId}/favorite`, {})
      .pipe(tap(() => this._favoritesResource.reload()));
  }

  removeFromFavoriteRecipe(recipeId: string) {
    return this.http
      .delete(`${this.apiUrl}/${recipeId}/favorite`)
      .pipe(tap(() => this._favoritesResource.reload()));
  }
}
