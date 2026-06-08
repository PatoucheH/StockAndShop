import { Component, computed, inject, signal } from '@angular/core';
import { RecipeService } from '../recipe.service';
import { ListRecipesComponent } from '../list-recipes/list-recipes';

@Component({
  selector: 'app-recipes-page',
  imports: [ListRecipesComponent],
  templateUrl: './recipes-page.html',
})
export class RecipesPageComponent {
  private recipeService = inject(RecipeService);

  showFavorites = signal(true);

  currentRecipes = computed(() =>
    this.showFavorites() ? this.recipeService.favoriteRecipes() : this.recipeService.recipes(),
  );
  currentIsLoading = computed(() =>
    this.showFavorites() ? this.recipeService.isFavoritesLoading() : this.recipeService.isLoading(),
  );
  currentHasError = computed(() =>
    this.showFavorites() ? this.recipeService.hasFavoritesError() : this.recipeService.hasError(),
  );
  currentHasMore = computed(() =>
    this.showFavorites() ? false : this.recipeService.hasMore(),
  );

  onLoadMore() {
    this.recipeService.loadNextPage();
  }
}
