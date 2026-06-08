import { Component, computed, inject, signal } from '@angular/core';
import { HomeService } from '../../../shared/services/home.service';
import { ProductStock } from '../../../shared/models/productStock.models';
import { RecipeService } from '../../recipe/recipe.service';
import { SmartUnitPipe } from '../../../shared/pipes/smart-unit.pipe';
import { UnitConversionService } from '../../../shared/services/unit-conversion.service';
import { StockRecipeModalComponent } from './stock-recipe-modal/stock-recipe-modal';

interface DecreaseEntry {
  amount: number;
  unit: string;
}

@Component({
  selector: 'app-list-stock',
  imports: [SmartUnitPipe, StockRecipeModalComponent],
  templateUrl: './list-stock.html',

})
export class ListStock {
  homeService = inject(HomeService);
  recipeService = inject(RecipeService);
  unitConversion = inject(UnitConversionService);

  modalRecipe = signal(false);

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
    this.decreaseState.update(state => ({
      ...state,
      [id]: { ...state[id], amount: Math.max(0.001, val) },
    }));
  }

  getUnit(id: number, unity: string): string {
    return this.decreaseState()[id]?.unit ?? this.unitConversion.getDefaultSubUnit(unity);
  }

  setUnit(id: number, unit: string) {
    this.decreaseState.update(state => ({
      ...state,
      [id]: { ...state[id], unit },
    }));
  }

  isWeightOrVolume(unity: string): boolean {
    return this.unitConversion.isWeightOrVolume(unity);
  }

  remove(item: ProductStock) {
    const unit = this.getUnit(item.id, item.unityProduct);
    const quantity = this.unitConversion.toBaseUnit(this.getAmount(item.id), unit);
    this.homeService.decreseStock({ name: item.nameProduct, quantity }).subscribe(() => {
      this.homeService.stockResource.reload();
    });
  }

  getRecipes() {
    this.recipeService.generateRecipe(this.homeService.selectedHome()!.id);
    this.modalRecipe.set(true);
  }
}
