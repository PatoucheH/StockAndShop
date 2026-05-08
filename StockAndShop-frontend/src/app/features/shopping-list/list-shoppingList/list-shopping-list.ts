import { Component, inject, OnInit, signal } from '@angular/core';
import { ShoppingListService } from '../shopping-list.service';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HomeService } from '../../home/home.service';
import { AddShoppingListComponent } from '../add-shopping-list/add-shopping-list';

@Component({
  selector: 'app-list-shopping-list',
  imports: [AddShoppingListComponent, RouterLink],
  templateUrl: './list-shopping-list.html',
  styleUrl: './list-shopping-list.scss',
})
export class ListShoppingListComponent implements OnInit {
  shoppingListService = inject(ShoppingListService);
  homeService = inject(HomeService);
  route = inject(ActivatedRoute);

  shoppingList = this.homeService.shoppingLists;

  modalIsOpen = signal<boolean>(false);

  ngOnInit() {}

  openModal() {
    this.modalIsOpen.set(true);
  }

  closeModal() {
    this.modalIsOpen.set(false);
    this.homeService.reloadShoppingList();
  }

  deleteShoppingList(id: number) {
    this.shoppingListService.deleteShoppingList(id).subscribe(() => {
      this.homeService.reloadShoppingList();
    });
  }
}
