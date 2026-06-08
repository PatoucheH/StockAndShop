import { Component, computed, input, output, signal } from '@angular/core';
import { RecipeCardComponent } from '../../../shared/components/recipe-card/recipe-card';
import { Recipe } from '../../../shared/models/recipe.models';
import { LoadingComponent } from '../../../shared/components/loading/loading';
import { ErrorComponent } from '../../../shared/components/error/error';
import { RecipeFilterBarComponent } from './recipe-filter-bar/recipe-filter-bar';
import { RecipeListItemComponent } from './recipe-list-item/recipe-list-item';

@Component({
  selector: 'app-list-recipes',
  imports: [RecipeCardComponent, LoadingComponent, ErrorComponent, RecipeFilterBarComponent, RecipeListItemComponent],
  templateUrl: './list-recipes.html',
})
export class ListRecipesComponent {
  recipes = input<Recipe[]>([]);
  isLoading = input(false);
  hasError = input(false);
  hasMore = input(false);
  loadMore = output<void>();

  selectedRecipe = signal<Recipe | null>(null);
  activeFilters = signal<string[]>([]);

  filteredRecipes = computed(() => {
    const filters = this.activeFilters();
    if (filters.length === 0) return this.recipes();
    return this.recipes().filter((r) =>
      filters.every((f) =>
        r.ingredients.some((i) => i.productName.toLowerCase().includes(f)),
      ),
    );
  });

  onFilterAdded(filter: string) {
    this.activeFilters.update((f) => [...f, filter]);
  }

  onFilterRemoved(filter: string) {
    this.activeFilters.update((f) => f.filter((x) => x !== filter));
  }

  onFiltersCleared() {
    this.activeFilters.set([]);
  }

  openRecipe(recipe: Recipe) {
    this.selectedRecipe.set(recipe);
  }

  closeRecipe() {
    this.selectedRecipe.set(null);
  }
}
