import { Component, inject, input, linkedSignal } from '@angular/core';
import { ProductItem } from '../../../shared/models/productItem.models';
import { ReactiveFormsModule } from '@angular/forms';
import { ProductService } from '../../../shared/services/product.service';
import { ShoppingListService } from '../shopping-list.service';
import { SmartUnitPipe } from '../../../shared/pipes/smart-unit.pipe';

@Component({
  selector: 'app-item-shopping-list',
  imports: [ReactiveFormsModule, SmartUnitPipe],
  templateUrl: './item-shopping-list.html',
  styleUrl: './item-shopping-list.scss',
})
export class ItemShoppingList {

  productService = inject(ProductService);
  shoppingListService = inject(ShoppingListService);

  item = input.required<ProductItem>();
  isChecked = linkedSignal(() => this.item().isChecked);

  checkedItem() {
    const newChecked = !this.isChecked();
    this.productService.checkedItemOnList(this.item().id).subscribe({
      next: () => {
        this.isChecked.set(newChecked);
        this.shoppingListService.updateItemCheckedState(this.item().id, newChecked);
      },
      error: (e) => console.error('Erreur lors de la mise à jour du product', e),
    });
  }
}
