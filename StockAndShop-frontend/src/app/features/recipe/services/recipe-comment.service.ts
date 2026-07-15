import { computed, inject, Injectable, signal } from '@angular/core';
import { HttpClient, httpResource } from '@angular/common/http';
import { environment } from '../../../../environments/environment';
import { RecipeComment, RecipeCommentRequest } from '../../../shared/models/recipe.models';
import { AuthService } from '../../auth/services/auth.service';
import { skip, tap } from 'rxjs';
import { takeUntilDestroyed, toObservable } from '@angular/core/rxjs-interop';

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

  constructor() {
    // The request URL doesn't change on login/logout, so httpResource won't refetch on its own —
    // force a reload so a same-tab account switch doesn't keep showing the previous user's comments.
    toObservable(this.authService.identityVersion).pipe(skip(1), takeUntilDestroyed()).subscribe(() => {
      this._commentsResource.reload();
    });
  }

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
