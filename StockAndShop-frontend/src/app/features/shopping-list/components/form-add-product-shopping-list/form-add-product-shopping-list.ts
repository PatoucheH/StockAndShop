import { Component, computed, inject, input, linkedSignal, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { toSignal, toObservable } from '@angular/core/rxjs-interop';
import { startWith, debounceTime, distinctUntilChanged, switchMap, of, tap, map } from 'rxjs';
import { ProductItemRequest } from '../../../../shared/models/productItem.models';
import { Product } from '../../../../shared/models/product.models';
import { ProductService } from '../../../../shared/services/product.service';
import { ShoppingListService } from '../../services/shopping-list.service';
import { UnitConversionService } from '../../../../shared/services/unit-conversion.service';
import { ToastService } from '../../../../core/services/toast.service';
import { BarcodeScanner } from '../barcode-scanner/barcode-scanner';
import { UnityLabelPipe } from '../../../../shared/pipes/unity-label.pipe';

@Component({
  selector: 'app-form-add-product-shopping-list',
  imports: [ReactiveFormsModule, BarcodeScanner, UnityLabelPipe],
  templateUrl: './form-add-product-shopping-list.html',
})
export class FormAddProductShoppingList {
  productService = inject(ProductService);
  shoppingListService = inject(ShoppingListService);
  unitConversion = inject(UnitConversionService);
  toast = inject(ToastService);

  id = input.required<string>();
  isFocused = signal(false);
  isExplicitlySelected = signal(false);

  // test implements scan code bar
  isScanning = signal(false);
  isLoadingBarcode = signal(false);
  onBarcodeDetected(barcode: string){
    this.isScanning.set(false);
    this.isLoadingBarcode.set(true);

    this.productService.getByBarcode(barcode).subscribe({
      next: (product) => {
        this.isLoadingBarcode.set(false);
        this.form.patchValue({ nameProduct: product.name, quantity: null });
        this.explicitProduct.set(product);
        this.isExplicitlySelected.set(true);
        this.toast.success(`${product.name} trouvé - ajustez la quantité et validez`);
      },
      error: () => {
        this.isLoadingBarcode.set(false);
        this.toast.error('Produit introuvable - essayer de le chercher manuellement');
      },
    });
  }

  form = new FormGroup({
    nameProduct: new FormControl('', Validators.required),
    quantity: new FormControl<number | null>(null, [Validators.required, Validators.min(0.001)]),
  });

  explicitProduct = signal<Product | null>(null);

  nameProduct = toSignal(
    this.form.controls.nameProduct.valueChanges.pipe(
      startWith(''),
      tap(() => {
        this.isExplicitlySelected.set(false);
        this.explicitProduct.set(null);
      })
    ),
    { initialValue: '' }
  );

  filteredProducts = toSignal(
    toObservable(this.nameProduct).pipe(
      debounceTime(150),
      distinctUntilChanged(),
      switchMap(term => (term && term.length >= 2)
        ? this.productService.searchByName(term)
        : of([])
      ),
      map(products => {
        const seen = new Set<string>();
        return products.filter(p => {
          const key = p.name.toLowerCase();
          if (seen.has(key)) return false;
          seen.add(key);
          return true;
        });
      })
    ),
    { initialValue: [] }
  );

  selectedProduct = computed(() => {
    const name = this.nameProduct()?.toLowerCase();
    const explicit = this.explicitProduct();
    if (explicit && explicit.name.toLowerCase() === name) return explicit;
    return this.filteredProducts().find((p) => p.name.toLowerCase() === name);
  });

  showDropdown = computed(() =>
    this.isFocused() && this.filteredProducts().length > 0 && !this.isExplicitlySelected()
  );

  // Unit chosen by the user; defaults to the product's first possible unit and resets when the product changes
  selectedUnity = linkedSignal(() => {
    const p = this.selectedProduct();
    return p?.unities?.[0] ?? p?.unity ?? '';
  });

  isWeightOrVolume = computed(() =>
    this.unitConversion.isWeightOrVolume(this.selectedUnity())
  );

  subUnit = linkedSignal(() =>
    this.unitConversion.getDefaultSubUnit(this.selectedUnity())
  );

  selectProduct(product: Product) {
    this.form.controls.nameProduct.setValue(product.name);
    this.explicitProduct.set(product);
    this.isExplicitlySelected.set(true);
    this.isFocused.set(false);
  }

  onFocus() { this.isFocused.set(true); }
  onBlur() { this.isFocused.set(false); }

  addProduct() {
    if (this.form.invalid || !this.selectedProduct()) return;
    const quantity = this.unitConversion.toBaseUnit(Number(this.form.value.quantity), this.subUnit());
    const payload: ProductItemRequest = {
      name: this.form.value.nameProduct!.toLowerCase(),
      quantity,
      unity: this.selectedUnity() || undefined,
    };
    this.shoppingListService
      .addProductToShoppingList(payload, Number(this.id()))
      .subscribe({
        next: () => {
          this.form.reset();
          this.toast.success('Produit ajouté à la liste');
        },
      });
  }
}
