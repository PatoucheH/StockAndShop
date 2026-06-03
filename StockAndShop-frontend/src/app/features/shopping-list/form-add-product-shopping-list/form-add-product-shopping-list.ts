import { Component, computed, inject, input } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProductItemRequest } from '../../../shared/models/productItem.models';
import { ProductService } from '../../../shared/services/product.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { startWith } from 'rxjs';
import { ShoppingListService } from '../shopping-list.service';

@Component({
  selector: 'app-form-add-product-shopping-list',
  imports: [ReactiveFormsModule],
  templateUrl: './form-add-product-shopping-list.html',
  styleUrl: './form-add-product-shopping-list.scss',
})
export class FormAddProductShoppingList {
  productService = inject(ProductService);
  shoppingListService = inject(ShoppingListService);

  products = this.productService.allProducts;

  id = input.required<string>();

  form = new FormGroup({
    nameProduct: new FormControl('', Validators.required),
    quantity: new FormControl<number | null>(null, [Validators.required, Validators.min(1)]),
  });

  nameProduct = toSignal(this.form.controls.nameProduct.valueChanges.pipe(startWith('')), {
    initialValue: '',
  });

  selectedProduct = computed(() => {
    const name = this.nameProduct()?.toLowerCase();
    return this.products().find((p) => p.name.toLowerCase() === name);
  });

  filteredProducts = computed(() => {
    const search = this.nameProduct()?.toLowerCase() || '';
    if (!search) return [];
    return this.products().filter((product) => product.name.toLowerCase().includes(search));
  });

  addProduct() {
    if (this.form.invalid || !this.selectedProduct()) return;
    const payload = {
      name: this.form.value.nameProduct,
      quantity: this.form.value.quantity,
    };
    this.shoppingListService
      .addProductToShoppingList(<ProductItemRequest>payload, Number(this.id()))
      .subscribe(() => this.form.reset());
  }
}
