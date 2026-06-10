import { Component, inject, signal } from '@angular/core';
import { HomeService } from '../../../shared/services/home.service';
import { AddHomeComponent } from '../add-home/add-home';
import { RouterLink } from '@angular/router';
import { FavoriteShoppingListsComponent } from '../../shopping-list/favorite-shopping-lists/favorite-shopping-lists';

@Component({
  selector: 'app-home',
  imports: [AddHomeComponent, RouterLink, FavoriteShoppingListsComponent],
  templateUrl: './home.html',
})
export class HomeComponent {
  homeService = inject(HomeService);

  homes = this.homeService.homes;

  modalIsOpen = signal(false);

  openModal() {
    this.modalIsOpen.set(true);
  }
}
