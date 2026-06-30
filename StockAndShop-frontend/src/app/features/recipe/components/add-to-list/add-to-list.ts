import { Component, computed, inject, input, signal } from '@angular/core';
import { ShoppingListService } from '../../../shopping-list/services/shopping-list.service';
import { ToastService } from '../../../../core/services/toast.service';
import { ProductItemRequest } from '../../../../shared/models/productItem.models';

@Component({
  selector: 'app-add-to-list',
  templateUrl: './add-to-list.html',
})
export class AddToListComponent {
  private shoppingListService = inject(ShoppingListService);
  private toast = inject(ToastService);

  products = input<ProductItemRequest[]>([]);
  label = input('Ajouter à la liste');

  allShoppingList = computed(() => this.shoppingListService.allShoppingListUser.value() ?? []);
  isLoadingLists = this.shoppingListService.allShoppingListUser.isLoading;
  selectedListId = signal<number | null>(null);

  onSelectChange(event: Event) {
    const id = Number((event.target as HTMLSelectElement).value);
    this.selectedListId.set(id || null);
  }

  addToList() {
    const listId = this.selectedListId();
    if (!listId) return;
    this.shoppingListService.addListProductsToShoppingList(this.products(), listId).subscribe({
      next: () => this.toast.success('Produits ajoutés à la liste'),
      error: () => this.toast.error("Problème lors de l'ajout des produits à la liste"),
    });
  }
}
