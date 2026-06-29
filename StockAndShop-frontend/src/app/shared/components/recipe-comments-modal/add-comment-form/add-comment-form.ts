import { Component, inject, input, output, signal } from '@angular/core';
import { RecipeCommentService } from '../../../../features/recipe/services/recipe-comment.service';
import { ToastService } from '../../../../core/services/toast.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-add-comment-form',
  imports: [FormsModule],
  templateUrl: './add-comment-form.html',
})
export class AddCommentFormComponent {
  private commentService = inject(RecipeCommentService);
  private toast = inject(ToastService);

  recipeId = input.required<string>();
  disabled = input(false);

  commentText = signal('');
  score = signal(0);
  hoveredScore = signal(0);
  isSubmitting = signal(false);

  readonly stars = [1, 2, 3, 4, 5];

  submit() {
    if (this.disabled() || this.score() === 0 || !this.commentText().trim() || this.isSubmitting()) return;
    this.isSubmitting.set(true);
    this.commentService
      .addComment({ comment: this.commentText(), score: this.score(), recipeId: this.recipeId() })
      .subscribe({
        next: () => {
          this.commentText.set('');
          this.score.set(0);
          this.isSubmitting.set(false);
          this.toast.success('Commentaire publié');
        },
        error: () => {
          this.isSubmitting.set(false);
          this.toast.error("Impossible d'ajouter le commentaire");
        },
      });
  }
}
