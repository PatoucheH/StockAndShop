import { Component, inject, input, linkedSignal } from '@angular/core';
import { ProductItem } from '../../../shared/models/productItem.models';
import { ReactiveFormsModule } from '@angular/forms';
import { ProductService } from '../../../shared/services/product.service';

@Component({
  selector: 'app-item-shopping-list',
  imports: [ReactiveFormsModule],
  templateUrl: './item-shopping-list.html',
  styleUrl: './item-shopping-list.scss',
})
export class ItemShoppingList {

  productService = inject(ProductService);

  item = input.required<ProductItem>();
  isChecked = linkedSignal(() => this.item().isChecked);

  checkedItem() {
    this.productService.checkedItemOnList(this.item().id).subscribe({
      next: () => this.isChecked.update(v => !v),
      error: (e) => console.error('Erreur lors de la mise à jour du product', e),
    });
  }
}
