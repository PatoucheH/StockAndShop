import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router} from '@angular/router';
import { HomeService } from '../home.service';
import { AuthService } from "../../auth/auth.service";
import { ConfirmModalComponent } from '../../../shared/components/confirm-modal/confirm-modal';
import {ListShoppingListComponent} from "../../shopping-list/list-shoppingList/list-shopping-list";

@Component({
  selector: 'app-details-home',
  imports: [ConfirmModalComponent, ListShoppingListComponent],
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

  view: 'list' | 'stock' | 'user' = 'list';
  showConfirmDelete = signal(false);

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) return;
    this.homeService.loadHomeById(id);
    this.homeService.loadShoppingListHomeById(id);
    this.homeService.loadUserHomeById(id);
  }

  changeView(newView: 'list' | 'stock' | 'user') {
    this.view = newView;
  }

  isOwner = computed(() => {
    const home = this.home();
    return home?.ownerEmail === this.authService.getUserEmail();
  });

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

}
