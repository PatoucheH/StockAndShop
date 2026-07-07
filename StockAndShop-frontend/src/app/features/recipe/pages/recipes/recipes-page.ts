import { Component, computed, inject, signal } from '@angular/core';
import { RecipeService } from '../../services/recipe.service';
import { ListRecipesComponent } from '../../components/list-recipes/list-recipes';

@Component({
  selector: 'app-recipes-page',
  imports: [ListRecipesComponent],
  templateUrl: './recipes-page.html',
})
export class RecipesPage {
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
  // Favorites are fully loaded at once — pagination only applies to the all-recipes view
  currentHasMore = computed(() =>
    this.showFavorites() ? false : this.recipeService.hasMore(),
  );

  onLoadMore() {
    this.recipeService.loadNextPage();
  }
}
