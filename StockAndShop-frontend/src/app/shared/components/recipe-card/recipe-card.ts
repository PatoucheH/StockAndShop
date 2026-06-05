import { Component, input, output } from '@angular/core';
import { Recipe } from '../../models/recipe.models';

@Component({
  selector: 'app-recipe-card',
  imports: [],
  templateUrl: './recipe-card.html',
})
export class RecipeCardComponent {
  recipe = input.required<Recipe>();
  close = output<void>();
}
