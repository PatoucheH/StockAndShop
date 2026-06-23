import { computed, inject, Injectable } from '@angular/core';
import { HttpClient, httpResource } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Product, ProductRequest } from '../models/product.models';
import { Observable } from 'rxjs';


@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/product`;

  readonly allUnitiesResource = httpResource<string[]>(() => `${this.apiUrl}/unity`);

  readonly allUnities = computed(() => this.allUnitiesResource.value() ?? []);

  createProduct(product: ProductRequest) {
    return this.http.post(`${this.apiUrl}`, product);
  }

  // Hits /product-list-item/{id}/check — the '-list-item' suffix is appended to the product apiUrl base
  checkedItemOnList(id: number) {
    return this.http.patch(`${this.apiUrl}-list-item/${id}/check`, {});
  }

  searchByName(name: string): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}?name=${encodeURIComponent(name)}`);
  }
}

