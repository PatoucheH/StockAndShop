import { Component, inject } from '@angular/core';
import { RecipeService } from '../../services/recipe.service';
import { HomeService } from '../../../../shared/services/home.service';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-btn-another-recipe',
  imports: [],
  templateUrl: './btn-another-recipe.html',
})
export class BtnAnotherRecipe {
  homeService = inject(HomeService);
  recipeService = inject(RecipeService);
  toast = inject(ToastService);

  generateNewRecipe() {
    // The global error interceptor handles the error toast
    this.recipeService.generateNewRecipe(this.homeService.selectedHome()!.id);
  }
}
