import { Component, computed, inject, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RecipeCardComponent } from '../../../shared/components/recipe-card/recipe-card';
import { Recipe } from '../../../shared/models/recipe.models';
import { ProductService } from '../../../shared/services/product.service';
import { LoadingComponent } from '../../../shared/components/loading/loading';
import { ErrorComponent } from '../../../shared/components/error/error';

@Component({
  selector: 'app-list-recipes',
  imports: [RecipeCardComponent, FormsModule, LoadingComponent, ErrorComponent],
  templateUrl: './list-recipes.html',
})
export class ListRecipesComponent {
  productService = inject(ProductService);

  recipes = input<Recipe[]>([]);
  isLoading = input(false);
  hasError = input(false);
  hasMore = input(false);
  loadMore = output<void>();

  selectedRecipe = signal<Recipe | null>(null);
  filterInput = signal('');
  activeFilters = signal<string[]>([]);

  filteredSuggestions = computed(() => {
    const search = this.filterInput().toLowerCase();
    if (!search) return [];
    return this.productService.allProducts().filter((p) =>
      p.name.toLowerCase().includes(search),
    );
  });

  filteredRecipes = computed(() => {
    const filters = this.activeFilters();
    if (filters.length === 0) return this.recipes();
    return this.recipes().filter((r) =>
      filters.every((filter) =>
        r.ingredients.some((i) => i.productName.toLowerCase().includes(filter)),
      ),
    );
  });

  addFilter() {
    const val = this.filterInput().trim().toLowerCase();
    if (!val || this.activeFilters().includes(val)) return;
    this.activeFilters.update((f) => [...f, val]);
    this.filterInput.set('');
  }

  removeFilter(filter: string) {
    this.activeFilters.update((f) => f.filter((x) => x !== filter));
  }

  clearFilters() {
    this.activeFilters.set([]);
    this.filterInput.set('');
  }

  openRecipe(recipe: Recipe) {
    this.selectedRecipe.set(recipe);
  }

  closeRecipe() {
    this.selectedRecipe.set(null);
  }
}
