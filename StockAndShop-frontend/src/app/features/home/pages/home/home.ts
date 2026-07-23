import { Component, computed, inject, signal } from '@angular/core';
import { HomeService } from '../../../../shared/services/home.service';
import { AddHomeComponent } from '../../components/add-home/add-home';
import { RouterLink } from '@angular/router';
import { FavoriteShoppingListsComponent } from '../../../shopping-list/components/favorite-shopping-lists/favorite-shopping-lists';
import { AuthService } from '../../../auth/services/auth.service';

@Component({
  selector: 'app-home',
  imports: [AddHomeComponent, RouterLink, FavoriteShoppingListsComponent],
  templateUrl: './home.html',
})
export class Home {
  private authService = inject(AuthService);
  private homeService = inject(HomeService);

  homes = this.homeService.homes;
  modalIsOpen = signal(false);

  readonly userName = computed(() => this.authService.getDisplayName() ?? '');

  openModal() {
    this.modalIsOpen.set(true);
  }
}
