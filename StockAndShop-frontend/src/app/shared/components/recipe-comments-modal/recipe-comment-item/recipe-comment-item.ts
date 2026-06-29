import { Component, computed, input } from '@angular/core';
import { RecipeComment } from '../../../models/recipe.models';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-recipe-comment-item',
  imports: [DatePipe],
  templateUrl: './recipe-comment-item.html',
})
export class RecipeCommentItemComponent {
  comment = input.required<RecipeComment>();

  stars = computed(() =>
    Array.from({ length: 5 }, (_, i) => i < this.comment().score),
  );

  displayName = computed(() => {
    const name = this.comment().username;
    return name.includes('@') ? name.split('@')[0] : name;
  });
}
