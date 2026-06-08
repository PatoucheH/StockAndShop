import { Component, input, output } from '@angular/core';
import { Recipe } from '../../../../shared/models/recipe.models';

@Component({
  selector: 'app-recipe-list-item',
  imports: [],
  templateUrl: './recipe-list-item.html',
})
export class RecipeListItemComponent {
  recipe = input.required<Recipe>();
  activeFilters = input<string[]>([]);
  selected = output<Recipe>();

  isIngredientHighlighted(productName: string): boolean {
    return this.activeFilters().some((f) =>
      productName.toLowerCase().includes(f),
    );
  }
}
