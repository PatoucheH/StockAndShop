import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient, httpResource } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { RecipeComment, RecipeCommentRequest } from '../../../shared/models/recipe.models';
import { AuthService } from '../../auth/services/auth.service';
import { tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class RecipeCommentService {
  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private apiUrl = `${environment.apiUrl}/recipe-comment`;

  private _recipeId = signal<string | null>(null);

  private _commentsResource = httpResource<RecipeComment[]>(() => {
    const id = this._recipeId();
    return id ? `${this.apiUrl}/recipe/${id}` : undefined;
  });

  readonly comments = computed(() =>
    [...(this._commentsResource.value() ?? [])].sort(
      (a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime(),
    ),
  );
  readonly isLoading = computed(() => this._commentsResource.isLoading());
  readonly hasError = computed(() => !!this._commentsResource.error());

  readonly hasUserCommented = computed(() => {
    const email = this.authService.getUserEmail();
    if (!email) return false;
    return this.comments().some((c) => c.username === email);
  });

  loadForRecipe(recipeId: string) {
    this._recipeId.set(recipeId);
  }

  addComment(request: RecipeCommentRequest) {
    return this.http.post<RecipeComment>(this.apiUrl, request)
      .pipe(tap(() => this._commentsResource.reload()));
  }

  deleteComment(id : number){
    return this.http.delete<RecipeComment>(`${this.apiUrl}/${id}`)
        .pipe(tap(() => this._commentsResource.reload()));
  }
}
