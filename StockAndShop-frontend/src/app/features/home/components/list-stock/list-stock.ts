import { Component, computed, inject, signal } from '@angular/core';
import { HomeService } from '../../../../shared/services/home.service';
import { ProductStock } from '../../../../shared/models/productStock.models';
import { RecipeService } from '../../../recipe/services/recipe.service';
import { NgClass } from '@angular/common';
import { SmartUnitPipe } from '../../../../shared/pipes/smart-unit.pipe';
import { UnitConversionService } from '../../../../shared/services/unit-conversion.service';
import { StockRecipeModalComponent } from './stock-recipe-modal/stock-recipe-modal';
import { ToastService } from '../../../../core/services/toast.service';
import { BtnAnotherRecipe } from '../../../recipe/components/btn-another-recipe/btn-another-recipe';

interface DecreaseEntry {
  amount: number;
  unit: string;
}

@Component({
  selector: 'app-list-stock',
  imports: [NgClass, SmartUnitPipe, StockRecipeModalComponent, BtnAnotherRecipe],
  templateUrl: './list-stock.html',
})
export class ListStock {
  homeService = inject(HomeService);
  recipeService = inject(RecipeService);
  unitConversion = inject(UnitConversionService);
  toast = inject(ToastService);

  modalRecipe = signal(false);
  selectedProducts = signal<ProductStock[]>([]);
  canGenerateRecipe = computed(() => {
    const selectedCount = this.selectedProducts().length;
    if (selectedCount > 0) {
      return selectedCount >= 5;
    }
    return this.homeService.stock().length >= 5;
  });

  groupedStock = computed(() => {
    const stock = this.homeService.stock();
    const map = new Map<string, ProductStock[]>();
    for (const s of stock) {
      const group = map.get(s.category) ?? [];
      group.push(s);
      map.set(s.category, group);
    }
    return Array.from(map.entries())
      .sort(([a], [b]) => a.localeCompare(b))
      .map(([category, items]) => ({ category, items }));
  });

  private decreaseState = signal<Record<number, DecreaseEntry>>({});

  getAmount(id: number): number {
    return this.decreaseState()[id]?.amount ?? 1;
  }

  setAmount(id: number, val: number) {
    this.decreaseState.update((state) => ({
      ...state,
      [id]: { ...state[id], amount: Math.max(0.001, val) },
    }));
  }

  getUnit(id: number, unity: string): string {
    return this.decreaseState()[id]?.unit ?? this.unitConversion.getDefaultSubUnit(unity);
  }

  setUnit(id: number, unit: string) {
    this.decreaseState.update((state) => ({
      ...state,
      [id]: { ...state[id], unit },
    }));
  }

  isWeightOrVolume(unity: string): boolean {
    return this.unitConversion.isWeightOrVolume(unity);
  }


  removeItem(s: ProductStock) {
    if (!s) return;
    const unit = this.getUnit(s.id, s.unityProduct);
    const quantity = this.unitConversion.toBaseUnit(this.getAmount(s.id), unit);
    this.homeService.decreseStock({ name: s.nameProduct, quantity, unity: s.unityProduct }).subscribe({
      next: () => {
        this.homeService.stockResource.reload();
        this.toast.success('Stock mis à jour');
      },
    });
  }

  getRecipes() {
    if (!this.canGenerateRecipe()) {
      this.toast.info('Au moins 5 produits sont nécessaires pour générer une recette.');
      return;
    }
    const selected = this.selectedProducts();
    const stockCount = this.homeService.stock().length;
    if (selected.length > 0 && selected.length < 5) {
      this.toast.error('Veuillez sélectionner au moins 5 produits pour générer une recette.');
      return;
    }
    if (selected.length === 0 && stockCount < 5) {
      this.toast.error('Votre stock doit contenir au moins 5 produits pour générer une recette.');
      return;
    }
    this.recipeService.clearNewRecipes();
    const homeId = this.homeService.selectedHome()!.id;
    this.recipeService.generateRecipe(homeId, selected.length > 0 ? selected : undefined, (msg) => {
      this.toast.error(msg);
      this.modalRecipe.set(false);
    });
    this.modalRecipe.set(true);
  }

  toggleProduct(product: ProductStock) {
    this.selectedProducts.update((products) => {
      const exists = products.some((p) => p.id === product.id);

      if (exists) {
        return products.filter((p) => p.id !== product.id);
      }

      return [...products, product];
    });
  }

  isSelected(product: ProductStock): boolean {
    return this.selectedProducts().some((p) => p.id === product.id);
  }
}
