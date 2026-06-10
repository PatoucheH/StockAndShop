import { Component, inject } from '@angular/core';
import { RecipeService } from '../recipe.service';
import { HomeService } from '../../../shared/services/home.service';

@Component({
  selector: 'app-btn-another-recipe',
  imports: [],
  templateUrl: './btn-another-recipe.html',
})
export class BtnAnotherRecipe {
  homeService = inject(HomeService);
  recipeService = inject(RecipeService);

  generateNewRecipe() {
    this.recipeService.generateNewRecipe(this.homeService.selectedHome()!.id);
  }
}
