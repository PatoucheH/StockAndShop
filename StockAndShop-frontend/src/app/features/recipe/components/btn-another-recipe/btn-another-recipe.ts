import { Component, inject } from '@angular/core';
import { RecipeService } from '../../recipe.service';
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
    this.recipeService.generateNewRecipe(this.homeService.selectedHome()!.id, (msg) => {
      this.toast.error(msg);
    });
  }
}
