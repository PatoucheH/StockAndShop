import { Component, computed, inject, input } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RecipeComment } from '../../../../shared/models/recipe.models';
import { StarRatingComponent } from '../../../../shared/components/star-rating/star-rating';
import { RecipeCommentService } from '../../services/recipe-comment.service';
import { ToastService } from '../../../../core/services/toast.service';
import { AuthService } from '../../../auth/services/auth.service';

@Component({
  selector: 'app-recipe-comment-item',
  imports: [DatePipe, StarRatingComponent],
  templateUrl: './recipe-comment-item.html',
})
export class RecipeCommentItemComponent {

  recipeCommentService = inject(RecipeCommentService);
  toastService = inject(ToastService);
  authService = inject(AuthService);
  comment = input.required<RecipeComment>();

  displayName = computed(() => {
    const name = this.comment().username;
    return name.includes('@') ? name.split('@')[0] : name;
  });

  isOwner = computed(() => this.comment().username === this.authService.getUserEmail());

  deleteComment(id: number) {
    this.recipeCommentService.deleteComment(id).subscribe({
      error: () => this.toastService.error("Erreur lors de la suppression du commentaire")
    })
  }
}
