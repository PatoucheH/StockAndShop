import { Component, inject, input, OnInit, output } from '@angular/core';
import { Recipe } from '../../models/recipe.models';
import { RecipeCommentService } from '../../../features/recipe/recipe-comment.service';
import { RecipeCommentItemComponent } from './recipe-comment-item/recipe-comment-item';
import { AddCommentFormComponent } from './add-comment-form/add-comment-form';
import { LoadingComponent } from '../loading/loading';

@Component({
  selector: 'app-recipe-comments-modal',
  imports: [RecipeCommentItemComponent, AddCommentFormComponent, LoadingComponent],
  templateUrl: './recipe-comments-modal.html',
})
export class RecipeCommentsModalComponent implements OnInit {
  private commentService = inject(RecipeCommentService);

  recipe = input.required<Recipe>();
  close = output<void>();

  comments = this.commentService.comments;
  isLoading = this.commentService.isLoading;
  hasError = this.commentService.hasError;
  hasUserCommented = this.commentService.hasUserCommented;

  ngOnInit() {
    this.commentService.loadForRecipe(this.recipe().id);
  }
}
