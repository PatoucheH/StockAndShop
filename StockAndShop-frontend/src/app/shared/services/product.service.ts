import { computed, inject, Injectable } from '@angular/core';
import { HttpClient, httpResource } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Product, ProductRequest } from '../models/product.models';


@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/product`;

  readonly allProductsResource = httpResource<Product[]>(() => `${this.apiUrl}`);
  readonly allUnitiesResource = httpResource<string[]>(() => `${this.apiUrl}/unity`);

  readonly allProducts = computed(() => this.allProductsResource.value() ?? []);
  readonly allUnities = computed(() => this.allUnitiesResource.value() ?? []);

  createProduct(product: ProductRequest){
    return this.http.post(`${this.apiUrl}`, product);
  }
}
