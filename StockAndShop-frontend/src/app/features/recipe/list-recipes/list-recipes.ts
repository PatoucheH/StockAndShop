import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RecipeService } from '../recipe.service';
import { RecipeCardComponent } from '../../../shared/components/recipe-card/recipe-card';
import { Recipe } from '../../../shared/models/recipe.models';


@Component({
  selector: 'app-list-recipes',
  imports: [RecipeCardComponent, FormsModule],
  templateUrl: './list-recipes.html',
})
export class ListRecipesComponent {
  recipeService = inject(RecipeService);

  isLoading = this.recipeService.isLoading;
  hasError = computed(() => !!this.recipeService.recipesResource.error());
  selectedRecipe = signal<Recipe | null>(null);
  ingredientFilter = signal('');

  filteredRecipes = computed(() => {
    const filter = this.ingredientFilter().trim().toLowerCase();
    if (!filter) return this.recipeService.recipes();
    return this.recipeService.recipes().filter((r) =>
      r.ingredients.some((i) => i.productName.toLowerCase().includes(filter)),
    );
  });

  openRecipe(recipe: Recipe) {
    this.selectedRecipe.set(recipe);
  }

  closeRecipe() {
    this.selectedRecipe.set(null);
  }
}
