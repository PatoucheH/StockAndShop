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

  addToFavoriteRecipe(recipeId: string){
    this.recipeService.addToFavoriteRecipe(recipeId);
  }
}
