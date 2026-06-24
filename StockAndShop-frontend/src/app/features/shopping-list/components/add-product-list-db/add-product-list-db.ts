import { Component, computed, inject, input, linkedSignal, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { toSignal } from '@angular/core/rxjs-interop';
import { startWith } from 'rxjs';
import { ProductService } from '../../../../shared/services/product.service';
import { CategoryService } from '../../../../shared/services/category.service';
import { ProductRequest } from '../../../../shared/models/product.models';
import { ProductItemRequest } from '../../../../shared/models/productItem.models';
import { ShoppingListService } from '../../shopping-list.service';
import { UnitConversionService } from '../../../../shared/services/unit-conversion.service';
import { UnityLabelPipe } from '../../../../shared/pipes/unity-label.pipe';
import { ToastService } from '../../../../core/services/toast.service';
import { FieldErrorComponent } from '../../../../shared/components/field-error/field-error';

@Component({
  selector: 'app-add-product-to-list',
  imports: [ReactiveFormsModule, UnityLabelPipe, FieldErrorComponent],
  templateUrl: './add-product-list-db.html',
})
export class AddProductListDb {
  fb = inject(FormBuilder);
  productService = inject(ProductService);
  categoryService = inject(CategoryService);
  shoppingListService = inject(ShoppingListService);
  unitConversion = inject(UnitConversionService);
  toast = inject(ToastService);

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

  selectedUnity = toSignal(this.form.controls.unity.valueChanges.pipe(startWith('')), {
    initialValue: '',
  });

  isWeightOrVolume = computed(() => this.unitConversion.isWeightOrVolume(this.selectedUnity()));

  subUnit = linkedSignal(() => this.unitConversion.getDefaultSubUnit(this.selectedUnity()));

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const formValue = this.form.getRawValue();

    const productToCreate: ProductRequest = {
      name: formValue.name,
      unity: formValue.unity,
      category: formValue.category,
    };

    this.productService.createProduct(productToCreate).subscribe({
      next: () => {
        const quantity = this.unitConversion.toBaseUnit(Number(formValue.quantity), this.subUnit());
        // Lowercased to match the server-normalized product name (backend stores all names in lowercase)
        const productToAdd: ProductItemRequest = {
          name: formValue.name.toLowerCase(),
          quantity,
        };
        this.shoppingListService
          .addProductToShoppingList(productToAdd, Number(this.id()))
          .subscribe({
            next: () => {
              this.toast.success('Produit créé et ajouté à la liste');
              this.closeModal.emit();
            },
            error: () => this.toast.error("Produit créé mais impossible de l'ajouter à la liste"),
          });
      },
      error: () => this.toast.error('Impossible de créer le produit'),
    });
  }
}
