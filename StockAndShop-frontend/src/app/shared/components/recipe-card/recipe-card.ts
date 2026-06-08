import { Component, inject, input, output } from '@angular/core';
import { Recipe } from '../../models/recipe.models';
import { RecipeService } from '../../../features/recipe/recipe.service';

@Component({
  selector: 'app-recipe-card',
  imports: [],
  templateUrl: './recipe-card.html',
})
export class RecipeCardComponent {
  recipeService = inject(RecipeService);
  recipe = input.required<Recipe>();
  close = output<void>();

  toggleFavorite(recipeId: string) {
    const action = this.recipeService.isFavorited(recipeId)
      ? this.recipeService.removeFromFavoriteRecipe(recipeId)
      : this.recipeService.addToFavoriteRecipe(recipeId);
    action.subscribe();
  }
}
