import { computed, effect, inject, Injectable, signal } from '@angular/core';
import { HttpClient, httpResource } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { PagedRecipeResponse, Recipe } from '../../shared/models/recipe.models';
import { skip, tap } from 'rxjs';
import { AuthService } from '../auth/auth.service';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';
import { ProductStock } from '../../shared/models/productStock.models';

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

  loadNextPage() {
    if (!this.hasMore() || this.isLoading()) return;
    this._page.update((p) => p + 1);
  }

  generateRecipe(homeId: string) {
    this.isGeneratingRecipe.set(true);
    this.http.get<Recipe[]>(`${this.apiUrl}/${homeId}/suggestions`).subscribe({
      next: (recipes) => {
        this._newRecipes.set(recipes);
        this.isGeneratingRecipe.set(false);
      },
      error: () => {
        this.isGeneratingRecipe.set(false);
      },
    });
  }

  generateNewRecipe(homeId: string, products?: ProductStock[]) {
    this.isGeneratingRecipe.set(true);
    const body = products?.length ? { products } : {};
    this.http.post<Recipe>(`${this.apiUrl}/${homeId}/generate`, body).subscribe({
      next: (recipe) => {
        this._newRecipes.update((prev) => [recipe, ...prev]);
        this.isGeneratingRecipe.set(false);
      },
      error: () => {
        this.isGeneratingRecipe.set(false);
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
