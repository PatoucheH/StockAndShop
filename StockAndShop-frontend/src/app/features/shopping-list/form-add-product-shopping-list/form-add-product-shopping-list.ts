import { Component, computed, effect, inject, input, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProductItemRequest } from '../../../shared/models/productItem.models';
import { ProductService } from '../../../shared/services/product.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { startWith } from 'rxjs';
import { ShoppingListService } from '../shopping-list.service';
import { UnitConversionService } from '../../../shared/services/unit-conversion.service';

@Component({
  selector: 'app-form-add-product-shopping-list',
  imports: [ReactiveFormsModule],
  templateUrl: './form-add-product-shopping-list.html',
  styleUrl: './form-add-product-shopping-list.scss',
})
export class FormAddProductShoppingList {
  productService = inject(ProductService);
  shoppingListService = inject(ShoppingListService);
  unitConversion = inject(UnitConversionService);

  products = this.productService.allProducts;

  id = input.required<string>();

  subUnit = signal<string>('g');

  form = new FormGroup({
    nameProduct: new FormControl('', Validators.required),
    quantity: new FormControl<number | null>(null, [Validators.required, Validators.min(0.001)]),
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

  isWeightOrVolume = computed(() => {
    const unity = this.selectedProduct()?.unity ?? '';
    return this.unitConversion.isWeightOrVolume(unity);
  });

  _ = effect(() => {
    const unity = this.selectedProduct()?.unity ?? '';
    this.subUnit.set(this.unitConversion.getDefaultSubUnit(unity));
  });

  addProduct() {
    if (this.form.invalid || !this.selectedProduct()) return;
    const quantity = this.unitConversion.toBaseUnit(Number(this.form.value.quantity), this.subUnit());
    const payload: ProductItemRequest = {
      name: this.form.value.nameProduct!,
      quantity,
    };
    this.shoppingListService
      .addProductToShoppingList(payload, Number(this.id()))
      .subscribe(() => this.form.reset());
  }
}
