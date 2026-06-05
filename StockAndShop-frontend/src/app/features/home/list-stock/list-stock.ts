import { Component, computed, inject, output, signal } from '@angular/core';
import { HomeService } from '../../../shared/services/home.service';
import { ProductStock } from '../../../shared/models/productStock.models';
import { RecipeService } from '../../recipe/recipe.service';
import { RecipeCardComponent } from '../../../shared/components/recipe-card/recipe-card';
import { Recipe } from '../../../shared/models/recipe.models';

@Component({
  selector: 'app-list-stock',
  imports: [RecipeCardComponent],
  templateUrl: './list-stock.html',
  styleUrl: './list-stock.scss',
})
export class ListStock {
  homeService = inject(HomeService);
  recipeService = inject(RecipeService);

  newRecipe: Recipe[] = [];

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

  decreaseAmounts: Record<number, number> = {};

  getAmount(id: number): number {
    return this.decreaseAmounts[id] ?? 1;
  }

  setAmount(id: number, val: number) {
    this.decreaseAmounts = { ...this.decreaseAmounts, [id]: Math.max(1, val) };
  }

  remove(item: ProductStock) {
    const quantity = this.getAmount(item.id);
    this.homeService.decreseStock({ name: item.nameProduct, quantity }).subscribe(() => {
      this.homeService.stockResource.reload();
    });
  }

  getRecipes() {
    this.recipeService.generateRecipe(this.homeService.selectedHome()!.id);
    this.modalRecipe.set(true);
  }

  closeModalRecipe(){
    this.modalRecipe.set(false);
  }
}
