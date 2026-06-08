import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ShoppingListService } from '../shopping-list.service';
import { LoadingComponent } from '../../../shared/components/loading/loading';
import { ErrorComponent } from '../../../shared/components/error/error';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-favorite-shopping-lists',
  imports: [RouterLink, LoadingComponent, ErrorComponent],
  templateUrl: './favorite-shopping-lists.html',
})
export class FavoriteShoppingListsComponent {
  shoppingListService = inject(ShoppingListService);
  toast = inject(ToastService);

  favoriteShoppingLists = this.shoppingListService.favoriteShoppingLists;
  isLoading = this.shoppingListService.isFavoritesLoading;
  hasError = this.shoppingListService.hasFavoritesError;

  removeFromFavorite(id: number) {
    this.shoppingListService.removeFromFavorite(id).subscribe({
      next: () => this.toast.success('Retiré des favoris'),
      error: () => this.toast.error('Impossible de retirer des favoris'),
    });
  }
}
