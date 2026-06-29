import { Component, effect, inject, output, signal } from '@angular/core';
import { RecipeService } from '../../../../recipe/services/recipe.service';
import { RecipeCardComponent } from '../../../../../shared/components/recipe-card/recipe-card';

@Component({
  selector: 'app-stock-recipe-modal',
  imports: [RecipeCardComponent],
  templateUrl: './stock-recipe-modal.html',
})
export class StockRecipeModalComponent {
  recipeService = inject(RecipeService);
  closed = output<void>();

  currentIndex = signal(0);

  constructor() {
    effect(() => {
      const count = this.recipeService.newRecipes().length;
      if (count > 0) this.currentIndex.set(0);
    });
  }

  prev() { this.currentIndex.update(i => i - 1); }
  next() { this.currentIndex.update(i => i + 1); }
}
