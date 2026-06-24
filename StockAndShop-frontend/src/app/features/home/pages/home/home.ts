import { Component, computed, inject, signal } from '@angular/core';
import { HomeService } from '../../../../shared/services/home.service';
import { AddHomeComponent } from '../../components/add-home/add-home';
import { RouterLink } from '@angular/router';
import { FavoriteShoppingListsComponent } from '../../../shopping-list/components/favorite-shopping-lists/favorite-shopping-lists';
import { AuthService } from '../../../auth/auth.service';

@Component({
  selector: 'app-home',
  imports: [AddHomeComponent, RouterLink, FavoriteShoppingListsComponent],
  templateUrl: './home.html',
})
export class HomeComponent {
  private authService = inject(AuthService);
  homeService = inject(HomeService);

  homes = this.homeService.homes;
  modalIsOpen = signal(false);

  readonly userName = computed(() => {
    const email = this.authService.getUserEmail() ?? '';
    const raw = email.split('@')[0];
    return raw.charAt(0).toUpperCase() + raw.slice(1);
  });

  openModal() {
    this.modalIsOpen.set(true);
  }
}
