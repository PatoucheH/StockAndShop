import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DecimalPipe } from '@angular/common';
import { SmartUnitPipe } from '../../../../shared/pipes/smart-unit.pipe';
import { Recipe } from '../../../../shared/models/recipe.models';
import { RecipeService } from '../../recipe.service';
import { RecipeCommentService } from '../../recipe-comment.service';
import { ShoppingListService } from '../../../shopping-list/shopping-list.service';
import { ToastService } from '../../../../core/services/toast.service';
import { LoadingComponent } from '../../../../shared/components/loading/loading';
import { ErrorComponent } from '../../../../shared/components/error/error';
import { RecipeCommentItemComponent } from '../../../../shared/components/recipe-comments-modal/recipe-comment-item/recipe-comment-item';
import { AddCommentFormComponent } from '../../../../shared/components/recipe-comments-modal/add-comment-form/add-comment-form';

@Component({
  selector: 'app-recipe-detail-page',
  imports: [DecimalPipe, SmartUnitPipe, LoadingComponent, ErrorComponent, RecipeCommentItemComponent, AddCommentFormComponent],
  templateUrl: './recipe-detail-page.html',
})
export class RecipeDetailPageComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  recipeService = inject(RecipeService);
  private commentService = inject(RecipeCommentService);
  shoppingListService = inject(ShoppingListService);
  private toast = inject(ToastService);

  readonly recipeId = this.route.snapshot.paramMap.get('id')!;

  // Lit depuis le cache du RecipeService, sans requête supplémentaire
  recipe = computed<Recipe | null>(() => {
    const id = this.recipeId;
    return (
      this.recipeService.recipes().find(r => r.id === id) ??
      this.recipeService.favoriteRecipes().find(r => r.id === id) ??
      null
    );
  });
  isLoading = computed(() =>
    (this.recipeService.isLoading() || this.recipeService.isFavoritesLoading()) && !this.recipe()
  );
  hasError = computed(() => !this.isLoading() && !this.recipe());

  tab = signal<'recipe' | 'reviews'>('recipe');
  selectedListId = signal<number | null>(null);
  allShoppingList = computed(() => this.shoppingListService.allShoppingListUser.value() ?? []);
  isLoadingLists = this.shoppingListService.allShoppingListUser.isLoading;

  comments = this.commentService.comments;
  commentsLoading = this.commentService.isLoading;
  hasUserCommented = this.commentService.hasUserCommented;

  scoreStars = computed(() => {
    const s = this.recipe()?.score;
    if (s == null) return null;
    return Array.from({ length: 5 }, (_, i) => i < Math.round(s));
  });

  ngOnInit() {
    this.commentService.loadForRecipe(this.recipeId);
  }

  goBack() {
    this.router.navigate(['/recipes']);
  }

  toggleFavorite() {
    const id = this.recipe()?.id;
    if (!id) return;
    const action = this.recipeService.isFavorited(id)
      ? this.recipeService.removeFromFavoriteRecipe(id)
      : this.recipeService.addToFavoriteRecipe(id);
    action.subscribe();
  }

  onSelectChange(event: Event) {
    const id = Number((event.target as HTMLSelectElement).value);
    this.selectedListId.set(id || null);
  }

  addToList() {
    const recipe = this.recipe();
    if (!recipe) return;
    const products = recipe.ingredients.map((p) => ({
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
