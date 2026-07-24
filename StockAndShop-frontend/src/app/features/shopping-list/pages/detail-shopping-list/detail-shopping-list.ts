import { Component, computed, effect, inject, signal } from '@angular/core';
import { ShoppingListService } from '../../services/shopping-list.service';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import { AddProductListDb } from '../../components/add-product-list-db/add-product-list-db';
import { ReactiveFormsModule } from '@angular/forms';
import { ItemShoppingList } from '../../components/item-shopping-list/item-shopping-list';
import { FormAddProductShoppingList } from '../../components/form-add-product-shopping-list/form-add-product-shopping-list';
import { HomeService } from '../../../../shared/services/home.service';
import { LoadingComponent } from '../../../../shared/components/loading/loading';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-detail-shopping-list',
  imports: [AddProductListDb, ReactiveFormsModule, ItemShoppingList, FormAddProductShoppingList, LoadingComponent],
  templateUrl: './detail-shopping-list.html',

})
export class DetailShoppingList{
  shoppingListService = inject(ShoppingListService);
  homeService = inject(HomeService);
  route = inject(ActivatedRoute);
  location = inject(Location);
  toast = inject(ToastService);

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
      ([category, items]) => ({
        category,
        // Number(false) = 0, Number(true) = 1 — unchecked items sort before checked ones
        items: [...items].sort((a, b) => Number(a.isChecked) - Number(b.isChecked)),
      })
    );
  });

  modalIsOpen = signal<boolean>(false);

  constructor() {
    effect(() => {
      const sl = this.selectedShoppingList();
      document.title = sl ? `${sl.name} — Stock&Shop` : 'Stock&Shop';
    });
  }

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
    const homeId = this.homeService.selectedHome()?.id ?? this.selectedShoppingList()?.homeId;
    if (!homeId) {
      this.toast.error('Impossible d\'identifier la maison');
      return;
    }
    this.shoppingListService.addToStockItemCheckedofShoppingList(homeId).subscribe({
      next: () => {
        this.homeService.stockResource.reload();
        this.toast.success('Produits ajoutés au stock');
      },
    });
  }

  toggleFavorite(id: number) {
    const action = this.shoppingListService.isFavorite(id)
      ? this.shoppingListService.removeFromFavorite(id)
      : this.shoppingListService.addToFavorite(id);
    action.subscribe();
  }
}
