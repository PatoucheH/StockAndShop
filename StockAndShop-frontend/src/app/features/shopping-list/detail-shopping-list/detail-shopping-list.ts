import { Component, computed, inject, signal } from '@angular/core';
import { ShoppingListService } from '../shopping-list.service';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import { AddProductListDb } from '../add-product-list-db/add-product-list-db';
import { ReactiveFormsModule } from '@angular/forms';
import { ItemShoppingList } from '../item-shopping-list/item-shopping-list';
import { FormAddProductShoppingList } from '../form-add-product-shopping-list/form-add-product-shopping-list';
import { HomeService } from '../../../shared/services/home.service';
import { LoadingComponent } from '../../../shared/components/loading/loading';

@Component({
  selector: 'app-detail-shopping-list',
  imports: [AddProductListDb, ReactiveFormsModule, ItemShoppingList, FormAddProductShoppingList, LoadingComponent],
  templateUrl: './detail-shopping-list.html',

})
export class DetailShoppingListComponent {
  shoppingListService = inject(ShoppingListService);
  homeService = inject(HomeService);
  route = inject(ActivatedRoute);
  location = inject(Location);

  id = this.route.snapshot.paramMap.get('id');
  selectedShoppingList = this.shoppingListService.selectedShoppingList;
  loading = this.shoppingListService.loading;

  groupedProducts = computed(() => {
    const products = this.selectedShoppingList()?.products ?? [];
    const map = new Map<string, typeof products>();
    for (const product of products) {
      const group = map.get(product.category) ?? [];
      group.push(product);
      map.set(product.category, group);
    }
    return Array.from(map.entries()).map(
      ([category, items]) => ({ category, items })
    );
  });

  modalIsOpen = signal<boolean>(false);

  ngOnInit() {
    if (!this.id) return;
    this.shoppingListService.selectShoppingList(Number(this.id));
  }

  openModal() {
    this.modalIsOpen.set(true);
  }

  closeModal() {
    this.modalIsOpen.set(false);
  }

  addToStockItemChecked(){
    const home = this.homeService.selectedHome();
    if (!home) return;
    this.shoppingListService.addToStockItemCheckedofShoppingList(home.id).subscribe({
      next: () => this.homeService.stockResource.reload(),
      error: (e) => console.error('Erreur lors de la mise à jour de la liste et du stock', e)
    });
  }

  toggleFavorite(id: number) {
    const action = this.shoppingListService.isFavorited(id)
      ? this.shoppingListService.removeFromFavorite(id)
      : this.shoppingListService.addToFavorite(id);
    action.subscribe();
  }
}
