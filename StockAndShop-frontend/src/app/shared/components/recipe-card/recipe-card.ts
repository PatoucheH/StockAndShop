import { Component, computed, inject, input, output, signal } from '@angular/core';
import { Recipe } from '../../models/recipe.models';
import { RecipeService } from '../../../features/recipe/recipe.service';
import { ShoppingListService } from '../../../features/shopping-list/shopping-list.service';
import { ProductRequest } from '../../models/product.models';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-recipe-card',
  imports: [],
  templateUrl: './recipe-card.html',
})
export class RecipeCardComponent {
  recipeService = inject(RecipeService);
  shoppingListService = inject(ShoppingListService);
  toast = inject(ToastService);
  recipe = input.required<Recipe>();
  close = output<void>();

  allShoppingList = computed(() => this.shoppingListService.allShoppingListUser.value() ?? []);
  isLoadingLists = this.shoppingListService.allShoppingListUser.isLoading;
  selectedListId = signal<number | null>(null);

  onSelectChange(event: Event) {
    const id = Number((event.target as HTMLSelectElement).value);
    this.selectedListId.set(id || null);
  }

  toggleFavorite(recipeId: string) {
    const action = this.recipeService.isFavorited(recipeId)
      ? this.recipeService.removeFromFavoriteRecipe(recipeId)
      : this.recipeService.addToFavoriteRecipe(recipeId);
    action.subscribe();
  }

  addToList() {
    console.log('selectedListId:', this.selectedListId());
    console.log('ingredients:', this.recipe().ingredients);
    const products = this.recipe().ingredients
      .map(p => ({
        name: p.productName,
        unity: p.unity,
        quantity: p.quantity
      }))
    this.shoppingListService.addListProductsToShoppingList(
      products,
      this.selectedListId()!
    ).subscribe({
      next: () => {
        this.toast.success("Produits ajoutés à la liste")
      },
      error: () => {
        this.toast.error("Problème lors de l'ajout des produits à la liste ")
      }
    });
  }
}
