import { computed, Injectable } from '@angular/core';
import { httpResource } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Category } from '../models/category.models';

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  private apiUrl = `${environment.apiUrl}/category`;

  readonly allCategoriesResource = httpResource<Category[]>(() => `${this.apiUrl}`);

  readonly allCategories = computed(() => this.allCategoriesResource.value() ?? []);
}
