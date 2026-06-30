import { Component, effect, inject, OnDestroy, output, signal } from '@angular/core';
import { RecipeService } from '../../../../recipe/services/recipe.service';
import { RecipeCardComponent } from '../../recipe-card/recipe-card';

@Component({
  selector: 'app-stock-recipe-modal',
  imports: [RecipeCardComponent],
  templateUrl: './stock-recipe-modal.html',
})
export class StockRecipeModalComponent implements OnDestroy {
  recipeService = inject(RecipeService);
  closed = output<void>();

  currentIndex = signal(0);

  constructor() {
    effect(() => {
      const count = this.recipeService.newRecipes().length;
      if (count > 0) this.currentIndex.set(0);
    });
  }

  ngOnDestroy() {
    this.recipeService.clearNewRecipes();
  }

  prev() { this.currentIndex.update(i => i - 1); }
  next() { this.currentIndex.update(i => i + 1); }
}
