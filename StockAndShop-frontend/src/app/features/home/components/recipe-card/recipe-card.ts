import { Component, computed, inject, input, output } from '@angular/core';
import { Router } from '@angular/router';
import { Recipe } from '../../../../shared/models/recipe.models';
import { ProductItemRequest } from '../../../../shared/models/productItem.models';
import { RecipeService } from '../../../recipe/services/recipe.service';
import { ToastService } from '../../../../core/services/toast.service';
import { StarRatingComponent } from '../../../../shared/components/star-rating/star-rating';
import { AddToListComponent } from '../../../recipe/components/add-to-list/add-to-list';

@Component({
  selector: 'app-recipe-card',
  imports: [StarRatingComponent, AddToListComponent],
  templateUrl: './recipe-card.html',
})
export class RecipeCardComponent {
  recipeService = inject(RecipeService);
  private router = inject(Router);
  private toast = inject(ToastService);

  recipe = input.required<Recipe>();
  close = output<void>();

  ingredientsAsRequest = computed<ProductItemRequest[]>(() =>
    this.recipe().ingredients.map(i => ({ name: i.productName, quantity: i.quantity }))
  );

  goToReviews() {
    this.router.navigate(['/recipes', this.recipe().id], { queryParams: { tab: 'reviews' } });
  }

  toggleFavorite(recipeId: string) {
    const action = this.recipeService.isFavorited(recipeId)
      ? this.recipeService.removeFromFavoriteRecipe(recipeId)
      : this.recipeService.addToFavoriteRecipe(recipeId);
    action.subscribe({
      error: () => this.toast.error('Impossible de modifier les favoris'),
    });
  }
}
