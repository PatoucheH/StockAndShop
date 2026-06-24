import { Component, inject, computed } from '@angular/core';
import { RouterLink, Router } from '@angular/router';
import { ShoppingListService } from '../../shopping-list.service';
import { LoadingComponent } from '../../../../shared/components/loading/loading';
import { ErrorComponent } from '../../../../shared/components/error/error';
import { ToastService } from '../../../../core/services/toast.service';
import { Carousel } from 'primeng/carousel';

@Component({
  selector: 'app-favorite-shopping-lists',
  imports: [RouterLink, LoadingComponent, ErrorComponent, Carousel],
  templateUrl: './favorite-shopping-lists.html',
})
export class FavoriteShoppingListsComponent {
  shoppingListService = inject(ShoppingListService);
  toast = inject(ToastService);
  router = inject(Router);

  favoriteShoppingLists = this.shoppingListService.favoriteShoppingLists;
  isLoading = this.shoppingListService.isFavoritesLoading;
  hasError = this.shoppingListService.hasFavoritesError;

  // Clamped so the carousel doesn't render empty slots when fewer than 3 lists exist
  numVisible = computed(() => Math.min(3, this.favoriteShoppingLists().length));

  responsiveOptions = computed(() => [
    { breakpoint: '1024px', numVisible: Math.min(3, this.favoriteShoppingLists().length), numScroll: 1 },
    { breakpoint: '768px', numVisible: Math.min(2, this.favoriteShoppingLists().length), numScroll: 1 },
    { breakpoint: '560px', numVisible: 1, numScroll: 1 },
  ]);

  removeFromFavorite(id: number) {
    this.shoppingListService.removeFromFavorite(id).subscribe({
      next: () => this.toast.success('Retiré des favoris'),
      error: () => this.toast.error('Impossible de retirer des favoris'),
    });
  }
}
