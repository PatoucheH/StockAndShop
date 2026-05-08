import { Component, inject, signal } from '@angular/core';
import { ShoppingListService } from '../shopping-list.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-detail-shopping-list',
  imports: [],
  templateUrl: './detail-shopping-list.html',
  styleUrl: './detail-shopping-list.scss',
})
export class DetailShoppingListComponent {
  shoppingListService = inject(ShoppingListService);
  route = inject(ActivatedRoute);

  selectedShoppingList = this.shoppingListService.selectedShoppingList;
  loading = this.shoppingListService.loading;

  modalIsOpen = signal<boolean>(false);

  ngOnInit() {
    const id= this.route.snapshot.paramMap.get('id');
    if (!id) return;
    this.shoppingListService.loadSelectedShoppingList(Number(id));
  }

  openModal() {
    this.modalIsOpen.set(true);
  }

  closeModal() {
    this.modalIsOpen.set(false);
  }
}
