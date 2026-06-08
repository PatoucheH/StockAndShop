import { Component, computed, effect, inject, input, output, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { toSignal } from '@angular/core/rxjs-interop';
import { startWith } from 'rxjs';
import { ProductService } from '../../../shared/services/product.service';
import { CategoryService } from '../../../shared/services/category.service';
import { ProductRequest } from '../../../shared/models/product.models';
import { ProductItemRequest } from '../../../shared/models/productItem.models';
import { ShoppingListService } from '../shopping-list.service';
import { UnitConversionService } from '../../../shared/services/unit-conversion.service';

@Component({
  selector: 'app-add-product-to-list',
  imports: [ReactiveFormsModule],
  templateUrl: './add-product-list-db.html',
  styleUrl: './add-product-list-db.scss',
})
export class AddProductListDb {
  fb = inject(FormBuilder);
  productService = inject(ProductService);
  categoryService = inject(CategoryService);
  shoppingListService = inject(ShoppingListService);
  unitConversion = inject(UnitConversionService);

  id = input.required<string>();
  closeModal = output<void>();

  categories = this.categoryService.allCategories;
  unities = this.productService.allUnities;

  form = this.fb.group({
    name: this.fb.nonNullable.control('', Validators.required),
    quantity: this.fb.nonNullable.control('', Validators.required),
    unity: this.fb.nonNullable.control('', Validators.required),
    category: this.fb.nonNullable.control('', Validators.required),
  });

  subUnit = signal<string>('g');

  selectedUnity = toSignal(this.form.controls.unity.valueChanges.pipe(startWith('')), {
    initialValue: '',
  });

  isWeightOrVolume = computed(() => this.unitConversion.isWeightOrVolume(this.selectedUnity()));

  _ = effect(() => {
    this.subUnit.set(this.unitConversion.getDefaultSubUnit(this.selectedUnity()));
  });

  submit() {
    if (this.form.invalid) return;
    const formValue = this.form.getRawValue();

    const productToCreate: ProductRequest = {
      name: formValue.name,
      unity: formValue.unity,
      category: formValue.category,
    };

    this.productService.createProduct(productToCreate).subscribe({
      next: () => {
        const quantity = this.unitConversion.toBaseUnit(Number(formValue.quantity), this.subUnit());
        const productToAdd: ProductItemRequest = {
          name: formValue.name.toLowerCase(),
          quantity,
        };
        this.shoppingListService
          .addProductToShoppingList(productToAdd, Number(this.id()))
          .subscribe({
            next: () => this.closeModal.emit(),
            error: () => {},
          });
      },
      error: () => {},
    });
  }
}
