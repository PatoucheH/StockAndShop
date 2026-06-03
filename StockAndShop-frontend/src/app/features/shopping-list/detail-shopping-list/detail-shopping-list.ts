import { Component, inject, signal } from '@angular/core';
import { ShoppingListService } from '../shopping-list.service';
import { ActivatedRoute } from '@angular/router';
import { AddProductListDb } from '../add-product-list-db/add-product-list-db';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { toSignal } from '@angular/core/rxjs-interop';
import { startWith } from 'rxjs';
import { ItemShoppingList } from '../item-shopping-list/item-shopping-list';
import { FormAddProductShoppingList } from '../form-add-product-shopping-list/form-add-product-shopping-list';

@Component({
  selector: 'app-detail-shopping-list',
  imports: [AddProductListDb, ReactiveFormsModule, ItemShoppingList, FormAddProductShoppingList],
  templateUrl: './detail-shopping-list.html',
  styleUrl: './detail-shopping-list.scss',
})
export class DetailShoppingListComponent {
  shoppingListService = inject(ShoppingListService);
  route = inject(ActivatedRoute);

  id = this.route.snapshot.paramMap.get('id');
  selectedShoppingList = this.shoppingListService.selectedShoppingList;
  loading = this.shoppingListService.loading;

  modalIsOpen = signal<boolean>(false);

  ngOnInit() {
    if (!this.id) return;
    this.shoppingListService.selectShoppingList(Number(this.id));
  }

  openModal() {
    this.modalIsOpen.set(true);
    console.log(this.selectedShoppingList());
  }

  closeModal() {
    this.modalIsOpen.set(false);
  }
}
