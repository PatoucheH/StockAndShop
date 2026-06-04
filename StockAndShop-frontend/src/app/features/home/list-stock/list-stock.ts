import { Component, computed, inject, Input, signal } from '@angular/core';
import { HomeService } from '../../../shared/services/home.service';
import { ProductStockDecrese } from '../../../shared/models/productStock.models';
import { form, FormField } from '@angular/forms/signals';

@Component({
  selector: 'app-list-stock',
  imports: [FormField],
  templateUrl: './list-stock.html',
  styleUrl: './list-stock.scss',
})
export class ListStock {
  homeService = inject(HomeService);

  groupedStock = computed(() => {
    const stock = this.homeService.stock();
    const map = new Map<string, typeof stock>();
    for (const s of stock) {
      const group = map.get(s.category) ?? [];
      group.push(s);
      map.set(s.category, group);
    }
    return Array.from(map.entries()).map(([category, items]) => ({ category, items }));
  });

  productStockForm = form(
    signal<ProductStockDecrese>({
      name: '',
      quantity: 1,
    }),
  );

  // decreseStock() {
  //
  //   this.homeService.decreseStock();
  // }
}
