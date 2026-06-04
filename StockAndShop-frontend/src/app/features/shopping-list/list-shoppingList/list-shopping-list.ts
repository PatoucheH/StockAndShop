import { Component, inject, OnInit, signal } from '@angular/core';
import { ShoppingListService } from '../shopping-list.service';
import { RouterLink } from '@angular/router';
import { HomeService } from '../../../shared/services/home.service';

@Component({
  selector: 'app-list-shopping-list',
  imports: [RouterLink],
  templateUrl: './list-shopping-list.html',
  styleUrl: './list-shopping-list.scss',
})
export class ListShoppingListComponent implements OnInit {
  shoppingListService = inject(ShoppingListService);
  homeService = inject(HomeService);

  shoppingList = this.homeService.shoppingLists;

  ngOnInit() {}

  deleteShoppingList(id: number) {
    this.shoppingListService.deleteShoppingList(id).subscribe(() => {
      this.homeService.shoppingListsResource.reload();
    });
  }
}
