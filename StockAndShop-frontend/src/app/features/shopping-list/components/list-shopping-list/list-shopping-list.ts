import { Component, inject, signal } from '@angular/core';
import { ShoppingListService } from '../../services/shopping-list.service';
import { RouterLink } from '@angular/router';
import { HomeService } from '../../../../shared/services/home.service';
import { ConfirmModalComponent } from '../../../../shared/components/confirm-modal/confirm-modal';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-list-shopping-list',
  imports: [RouterLink, ConfirmModalComponent],
  templateUrl: './list-shopping-list.html',
})
export class ListShoppingListComponent {
  shoppingListService = inject(ShoppingListService);
  homeService = inject(HomeService);
  toast = inject(ToastService);

  shoppingList = this.homeService.shoppingLists;
  pendingDeleteId = signal<number | null>(null);

  confirmDelete(id: number) {
    this.pendingDeleteId.set(id);
  }

  onDeleteConfirmed() {
    const id = this.pendingDeleteId();
    if (id === null) return;
    this.shoppingListService.deleteShoppingList(id).subscribe({
      next: () => {
        this.homeService.shoppingListsResource.reload();
        this.toast.success('Liste supprimée');
      },
      error: () => this.toast.error('Impossible de supprimer la liste'),
    });
    this.pendingDeleteId.set(null);
  }

}
