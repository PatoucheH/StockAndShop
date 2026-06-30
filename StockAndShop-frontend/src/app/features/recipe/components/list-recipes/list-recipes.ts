import { Component, computed, input, output, signal } from '@angular/core';
import { Recipe } from '../../../../shared/models/recipe.models';
import { LoadingComponent } from '../../../../shared/components/loading/loading';
import { ErrorComponent } from '../../../../shared/components/error/error';
import { RecipeFilterBarComponent } from './recipe-filter-bar/recipe-filter-bar';
import { RecipeListItemComponent } from './recipe-list-item/recipe-list-item';

@Component({
  selector: 'app-list-recipes',
  imports: [LoadingComponent, ErrorComponent, RecipeFilterBarComponent, RecipeListItemComponent],
  templateUrl: './list-recipes.html',
})
export class ListRecipesComponent {
  recipes = input<Recipe[]>([]);
  isLoading = input(false);
  hasError = input(false);
  hasMore = input(false);
  loadMore = output<void>();

  activeFilters = signal<string[]>([]);
  activeTags = signal<string[]>([]);
  nameFilter = signal('');

  filteredRecipes = computed(() => {
    const filters = this.activeFilters();
    const tags = this.activeTags();
    const name = this.nameFilter().toLowerCase().trim();
    return this.recipes()
      .filter((r) => !name || r.title.toLowerCase().includes(name))
      .filter((r) =>
        filters.length === 0 ||
        filters.every((f) => r.ingredients.some((i) => i.productName.toLowerCase().includes(f))),
      )
      .filter((r) =>
        tags.length === 0 ||
        tags.some((t) => r.tags.includes(t)),
      );
  });

  onNameFilterChanged(name: string) { this.nameFilter.set(name); }
  onFilterAdded(filter: string) { this.activeFilters.update((f) => [...f, filter]); }
  onFilterRemoved(filter: string) { this.activeFilters.update((f) => f.filter((x) => x !== filter)); }
  onFiltersCleared() { this.activeFilters.set([]); }
  onTagToggled(tag: string) {
    this.activeTags.update((tags) =>
      tags.includes(tag) ? tags.filter((t) => t !== tag) : [...tags, tag],
    );
  }
  onTagFiltersCleared() { this.activeTags.set([]); }
}
