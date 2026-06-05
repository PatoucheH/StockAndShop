import { computed, Injectable } from '@angular/core';
import { httpResource } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Recipe } from '../../shared/models/recipe.models';

@Injectable({ providedIn: 'root' })
export class RecipeService {
  private apiUrl = `${environment.apiUrl}/recipe`;

  readonly recipesResource = httpResource<Recipe[]>(() => this.apiUrl);

  readonly isLoading = computed(() => this.recipesResource.isLoading());

  readonly recipes = computed(() => {
    if (this.recipesResource.error()) return [];
    return this.recipesResource.value() ?? [];
  });
}
