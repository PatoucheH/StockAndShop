import { Component, inject, output } from '@angular/core';
import { RecipeService } from '../../../recipe/recipe.service';
import { RecipeCardComponent } from '../../../../shared/components/recipe-card/recipe-card';

@Component({
  selector: 'app-stock-recipe-modal',
  imports: [RecipeCardComponent],
  templateUrl: './stock-recipe-modal.html',
})
export class StockRecipeModalComponent {
  recipeService = inject(RecipeService);
  closed = output<void>();
}
