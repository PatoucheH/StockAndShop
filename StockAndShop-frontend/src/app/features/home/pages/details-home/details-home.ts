import { Component, computed, effect, inject, linkedSignal, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HomeService } from '../../../../shared/services/home.service';
import { AuthService } from '../../../auth/services/auth.service';
import { ConfirmModalComponent } from '../../../../shared/components/confirm-modal/confirm-modal';
import { ListShoppingListComponent } from '../../../shopping-list/components/list-shopping-list/list-shopping-list';
import { AddShoppingListComponent } from '../../../shopping-list/components/add-shopping-list/add-shopping-list';
import { ListStock } from '../../components/list-stock/list-stock';
import { ManageUsersHomeComponent } from '../../components/manage-users-home/manage-users-home';
import { LoadingComponent } from '../../../../shared/components/loading/loading';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-details-home',
  imports: [ConfirmModalComponent, ListShoppingListComponent, AddShoppingListComponent, ListStock, ManageUsersHomeComponent, LoadingComponent],
  templateUrl: './details-home.html',

})
export class DetailsHomeComponent implements OnInit {
  authService = inject(AuthService);
  router = inject(Router);
  homeService = inject(HomeService);
  route = inject(ActivatedRoute);

  home = this.homeService.selectedHome;
  shoppingLists = this.homeService.shoppingLists;
  stock = this.homeService.stock;

  // Resets to the 'list' tab whenever the selected home changes
  view = linkedSignal<'list' | 'stock' | 'user'>(() => {
    this.home();
    return 'list';
  });

  showConfirmDelete = signal(false);
  showAddList = signal(false);

  isOwner = computed(() => this.home()?.ownerEmail === this.authService.getUserEmail());

  constructor() {
    effect(() => {
      const home = this.home();
      document.title = home ? `${home.name} — Stock&Shop` : 'Stock&Shop';
    });

    effect(() => {
      const err = this.homeService.usersResource.error();
      if (err instanceof HttpErrorResponse && err.status === 403) {
        this.router.navigate(['/']);
      }
    });
  }

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) return;
    this.homeService.selectHome(id);
  }

  changeView(newView: 'list' | 'stock' | 'user') {
    this.view.set(newView);
  }

  deleteHome() {
    this.showConfirmDelete.set(true);
  }

  onDeleteConfirmed() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) return;
    this.homeService.deleteHome(id).subscribe(() => {
      this.router.navigate(['/']);
    });
  }

  toggleAddShoppingListModal() {
    this.showAddList.update((value) => !value);
  }
}
