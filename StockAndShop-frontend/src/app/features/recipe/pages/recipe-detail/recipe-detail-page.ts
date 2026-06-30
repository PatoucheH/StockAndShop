import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SmartUnitPipe } from '../../../../shared/pipes/smart-unit.pipe';
import { StarRatingComponent } from '../../../../shared/components/star-rating/star-rating';
import { Recipe } from '../../../../shared/models/recipe.models';
import { ProductItemRequest } from '../../../../shared/models/productItem.models';
import { RecipeService } from '../../services/recipe.service';
import { RecipeCommentService } from '../../services/recipe-comment.service';
import { ToastService } from '../../../../core/services/toast.service';
import { LoadingComponent } from '../../../../shared/components/loading/loading';
import { ErrorComponent } from '../../../../shared/components/error/error';
import { AddToListComponent } from '../../components/add-to-list/add-to-list';
import { RecipeCommentItemComponent } from '../../components/recipe-comment-item/recipe-comment-item';
import { AddCommentFormComponent } from '../../components/add-comment-form/add-comment-form';
import { CookRecipeModalComponent } from '../../components/cook-recipe-modal/cook-recipe-modal';

@Component({
  selector: 'app-recipe-detail-page',
  imports: [SmartUnitPipe, StarRatingComponent, LoadingComponent, ErrorComponent, AddToListComponent, RecipeCommentItemComponent, AddCommentFormComponent, CookRecipeModalComponent],
  templateUrl: './recipe-detail-page.html',
})
export class RecipeDetailPageComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  recipeService = inject(RecipeService);
  private commentService = inject(RecipeCommentService);
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
  showCookModal = signal(false);

  ingredientsAsRequest = computed<ProductItemRequest[]>(() =>
    (this.recipe()?.ingredients ?? []).map(i => ({ name: i.productName, quantity: i.quantity }))
  );

  comments = this.commentService.comments;
  commentsLoading = this.commentService.isLoading;
  hasUserCommented = this.commentService.hasUserCommented;

  ngOnInit() {
    this.commentService.loadForRecipe(this.recipeId);
    if (this.route.snapshot.queryParamMap.get('tab') === 'reviews') {
      this.tab.set('reviews');
    }
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
    action.subscribe({
      error: () => this.toast.error('Impossible de modifier les favoris'),
    });
  }
}
