import { Component, computed, inject, input, OnInit, output, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { Recipe } from '../../models/recipe.models';
import { RecipeService } from '../../../features/recipe/services/recipe.service';
import { ShoppingListService } from '../../../features/shopping-list/services/shopping-list.service';
import { ToastService } from '../../../core/services/toast.service';
import { RecipeCommentService } from '../../../features/recipe/services/recipe-comment.service';
import { RecipeCommentsModalComponent } from '../recipe-comments-modal/recipe-comments-modal';

@Component({
  selector: 'app-recipe-card',
  imports: [RecipeCommentsModalComponent, DecimalPipe],
  templateUrl: './recipe-card.html',
})
export class RecipeCardComponent implements OnInit {
  recipeService = inject(RecipeService);
  shoppingListService = inject(ShoppingListService);
  toast = inject(ToastService);
  commentService = inject(RecipeCommentService);

  recipe = input.required<Recipe>();
  close = output<void>();

  allShoppingList = computed(() => this.shoppingListService.allShoppingListUser.value() ?? []);
  isLoadingLists = this.shoppingListService.allShoppingListUser.isLoading;
  selectedListId = signal<number | null>(null);
  showCommentsModal = signal(false);

  commentCount = computed(() => this.commentService.comments().length);

  scoreStars = computed(() => {
    const s = this.recipe().score;
    if (s === null) return null;
    return Array.from({ length: 5 }, (_, i) => i < Math.round(s));
  });

  ngOnInit() {
    this.commentService.loadForRecipe(this.recipe().id);
  }

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
    const products = this.recipe().ingredients.map((p) => ({
      name: p.productName,
      unity: p.unity,
      quantity: p.quantity,
    }));
    this.shoppingListService.addListProductsToShoppingList(products, this.selectedListId()!).subscribe({
      next: () => this.toast.success('Produits ajoutés à la liste'),
      error: () => this.toast.error("Problème lors de l'ajout des produits à la liste"),
    });
  }
}
