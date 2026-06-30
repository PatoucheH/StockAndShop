import { Component, computed, effect, inject, input, output, signal } from '@angular/core';
import { forkJoin } from 'rxjs';
import { HomeService } from '../../../../shared/services/home.service';
import { ToastService } from '../../../../core/services/toast.service';
import { SmartUnitPipe } from '../../../../shared/pipes/smart-unit.pipe';
import { AddToListComponent } from '../add-to-list/add-to-list';
import { Recipe, RecipeIngredient } from '../../../../shared/models/recipe.models';
import { ProductItemRequest } from '../../../../shared/models/productItem.models';

interface IngredientCheck {
  ingredient: RecipeIngredient;
  inStock: boolean;
  stockQty: number;
}

@Component({
  selector: 'app-cook-recipe-modal',
  imports: [SmartUnitPipe, AddToListComponent],
  templateUrl: './cook-recipe-modal.html',
})
export class CookRecipeModalComponent {
  private homeService = inject(HomeService);
  private toast = inject(ToastService);

  recipe = input.required<Recipe>();
  closed = output<void>();

  homes = computed(() => this.homeService.homes());
  stock = computed(() => this.homeService.stock());
  isLoadingHomes = computed(() => this.homeService.homesResource.isLoading());
  isLoadingStock = computed(() => this.homeService.stockResource.isLoading());
  noHomes = computed(() => !this.isLoadingHomes() && this.homes().length === 0);
  selectedHomeId = signal<string | null>(null);
  isCooking = signal(false);

  showHomePicker = computed(() => this.homes().length > 1 && !this.selectedHomeId());

  checks = computed<IngredientCheck[]>(() => {
    if (this.isLoadingStock() || this.showHomePicker() || !this.selectedHomeId()) return [];
    const stock = this.stock();
    return this.recipe().ingredients.map((ingredient: RecipeIngredient) => {
      const stockItem = stock.find(
        s => s.nameProduct.toLowerCase() === ingredient.productName.toLowerCase()
      );
      const stockQty = stockItem?.quantity ?? 0;
      return { ingredient, inStock: stockQty >= ingredient.quantity, stockQty };
    });
  });

  missingIngredients = computed(() => this.checks().filter(c => !c.inStock));
  allAvailable = computed(() => this.checks().length > 0 && this.missingIngredients().length === 0);

  missingAsRequest = computed<ProductItemRequest[]>(() =>
    this.missingIngredients().map(c => ({
      name: c.ingredient.productName,
      quantity: c.ingredient.quantity - c.stockQty,
    }))
  );

  constructor() {
    effect(() => {
      const homes = this.homes();
      if (homes.length === 1 && !this.selectedHomeId()) {
        this.homeService.selectHome(homes[0].id);
        this.selectedHomeId.set(homes[0].id);
      }
    });
  }

  onHomeChange(event: Event) {
    const id = (event.target as HTMLSelectElement).value;
    if (id) {
      this.homeService.selectHome(id);
      this.selectedHomeId.set(id);
    }
  }

  cookAndConsume() {
    this.isCooking.set(true);
    forkJoin(
      this.recipe().ingredients.map((i: RecipeIngredient) =>
        this.homeService.decreseStock({ name: i.productName, quantity: i.quantity })
      )
    ).subscribe({
      next: () => {
        this.homeService.stockResource.reload();
        this.toast.success('Ingrédients retirés du stock. Bonne cuisine !');
        this.isCooking.set(false);
        this.closed.emit();
      },
      error: () => {
        this.toast.error('Erreur lors du retrait des ingrédients');
        this.isCooking.set(false);
      },
    });
  }
}
