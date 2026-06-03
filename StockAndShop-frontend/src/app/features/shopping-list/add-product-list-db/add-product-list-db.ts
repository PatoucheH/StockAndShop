import { Component, inject, input, output } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ProductService } from '../../../shared/services/product.service';
import { CategoryService } from '../../../shared/services/category.service';
import { ProductRequest } from '../../../shared/models/product.models';
import { ProductItemRequest } from '../../../shared/models/productItem.models';
import { ShoppingListService } from '../shopping-list.service';

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
        const productToAdd: ProductItemRequest = {
          name: formValue.name.toLowerCase(),
          quantity: Number(formValue.quantity),
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
