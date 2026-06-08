import { Component, computed, inject, input, linkedSignal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProductItemRequest } from '../../../shared/models/productItem.models';
import { ProductService } from '../../../shared/services/product.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { startWith } from 'rxjs';
import { ShoppingListService } from '../shopping-list.service';
import { UnitConversionService } from '../../../shared/services/unit-conversion.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-form-add-product-shopping-list',
  imports: [ReactiveFormsModule],
  templateUrl: './form-add-product-shopping-list.html',
})
export class FormAddProductShoppingList {
  productService = inject(ProductService);
  shoppingListService = inject(ShoppingListService);
  unitConversion = inject(UnitConversionService);
  toast = inject(ToastService);

  products = this.productService.allProducts;
  id = input.required<string>();

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

  isWeightOrVolume = computed(() =>
    this.unitConversion.isWeightOrVolume(this.selectedProduct()?.unity ?? '')
  );

  subUnit = linkedSignal(() =>
    this.unitConversion.getDefaultSubUnit(this.selectedProduct()?.unity ?? '')
  );

  addProduct() {
    if (this.form.invalid || !this.selectedProduct()) return;
    const quantity = this.unitConversion.toBaseUnit(Number(this.form.value.quantity), this.subUnit());
    const payload: ProductItemRequest = {
      name: this.form.value.nameProduct!,
      quantity,
    };
    this.shoppingListService
      .addProductToShoppingList(payload, Number(this.id()))
      .subscribe({
        next: () => {
          this.form.reset();
          this.toast.success('Produit ajouté à la liste');
        },
        error: () => this.toast.error("Impossible d'ajouter le produit"),
      });
  }
}
