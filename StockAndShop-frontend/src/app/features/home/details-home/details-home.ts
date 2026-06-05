import { Component, computed, effect, inject, linkedSignal, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HomeService } from '../../../shared/services/home.service';
import { AuthService } from '../../auth/auth.service';
import { ConfirmModalComponent } from '../../../shared/components/confirm-modal/confirm-modal';
import { ListShoppingListComponent } from '../../shopping-list/list-shoppingList/list-shopping-list';
import { AddShoppingListComponent } from '../../shopping-list/add-shopping-list/add-shopping-list';
import { ListStock } from '../list-stock/list-stock';

@Component({
  selector: 'app-details-home',
  imports: [ConfirmModalComponent, ListShoppingListComponent, AddShoppingListComponent, ListStock],
  templateUrl: './details-home.html',
  styleUrl: './details-home.scss',
})
export class DetailsHomeComponent implements OnInit {
  authService = inject(AuthService);
  router = inject(Router);
  homeService = inject(HomeService);
  route = inject(ActivatedRoute);

  home = this.homeService.selectedHome;
  shoppingLists = this.homeService.shoppingLists;
  users = this.homeService.users;
  stock = this.homeService.stock;

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

  onFindRecipes() {
    this.router.navigate(['/recipes']);
  }
}
