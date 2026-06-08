import { computed, effect, inject, Injectable, signal, untracked } from '@angular/core';
import { HttpClient, httpResource } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { PagedRecipeResponse, Recipe } from '../../shared/models/recipe.models';

@Injectable({ providedIn: 'root' })
export class RecipeService {
  private apiUrl = `${environment.apiUrl}/recipe`;
  private http = inject(HttpClient);
  private readonly pageSize = 50;

  private _page = signal(0);
  private _allRecipes = signal<Recipe[]>([]);
  private _newRecipes = signal<Recipe[]>([]);

  private _resource = httpResource<PagedRecipeResponse>(() =>
    `${this.apiUrl}?page=${this._page()}&size=${this.pageSize}`,
  );

  constructor() {
    effect(
      () => {
        if (this._resource.isLoading()) return;
        const response = this._resource.value();
        if (!response) return;
        const page = untracked(() => this._page());
        if (page === 0) {
          this._allRecipes.set(response.recipes);
        } else {
          this._allRecipes.update((prev) => [...prev, ...response.recipes]);
        }
      },
      { allowSignalWrites: true },
    );
  }

  private _favoritesResource = httpResource<Recipe[]>(() => `${this.apiUrl}/favorites`);

  readonly isLoading = computed(() => this._resource.isLoading());
  readonly hasError = computed(() => !!this._resource.error());
  readonly recipes = computed(() => this._allRecipes());
  readonly newRecipes = computed(() => this._newRecipes());
  readonly hasMore = computed(() => this._resource.value()?.hasMore ?? false);

  readonly favoriteRecipes = computed(() => this._favoritesResource.value() ?? []);
  readonly isFavoritesLoading = computed(() => this._favoritesResource.isLoading());
  readonly hasFavoritesError = computed(() => !!this._favoritesResource.error());

  loadNextPage() {
    if (!this.hasMore() || this.isLoading()) return;
    this._page.update((p) => p + 1);
  }

  generateRecipe(homeId: string) {
    this.http.get<Recipe[]>(`${this.apiUrl}/${homeId}/suggestions`).subscribe((recipes) => {
      this._newRecipes.set(recipes);
    });
  }

  addToFavoriteRecipe(recipeId: string){
    this.http.post(`${this.apiUrl}/${recipeId}/favorite`, {}).subscribe((recipe) => {
      console.log(`add to favorite recipe ${recipeId}`);
    });
  }
}
