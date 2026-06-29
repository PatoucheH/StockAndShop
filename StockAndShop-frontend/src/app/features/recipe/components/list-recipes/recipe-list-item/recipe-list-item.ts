import { Component, inject, input } from '@angular/core';
import { Router } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { Recipe } from '../../../../../shared/models/recipe.models';

@Component({
  selector: 'app-recipe-list-item',
  imports: [DecimalPipe],
  templateUrl: './recipe-list-item.html',
})
export class RecipeListItemComponent {
  private router = inject(Router);

  recipe = input.required<Recipe>();
  activeFilters = input<string[]>([]);

  navigate() {
    this.router.navigate(['/recipes', this.recipe().id]);
  }

  isIngredientHighlighted(productName: string): boolean {
    return this.activeFilters().some((f) => productName.toLowerCase().includes(f));
  }
}
