import { Component, computed, inject, signal } from '@angular/core';
import { HomeService } from '../../../shared/services/home.service';
import { AuthService } from '../../auth/auth.service';
import { UserSearchResult } from '../../../shared/models/user.models';
import { UserSearchInputComponent } from './user-search-input/user-search-input';
import { HomeMemberListComponent } from './home-member-list/home-member-list';

@Component({
  selector: 'app-manage-users-home',
  imports: [UserSearchInputComponent, HomeMemberListComponent],
  templateUrl: './manage-users-home.html',

})
export class ManageUsersHomeComponent {
  private homeService = inject(HomeService);
  private authService = inject(AuthService);

  users = this.homeService.users;
  home = this.homeService.selectedHome;
  isOwner = computed(() => this.home()?.ownerEmail === this.authService.getUserEmail());

  showAddForm = signal(false);
  errorMessage = signal<string | null>(null);

  toggleAddForm() {
    this.showAddForm.update(v => !v);
    if (!this.showAddForm()) this.errorMessage.set(null);
  }

  onUserSubmitted({ user, role }: { user: UserSearchResult; role: 'USER' | 'VIEWER' }) {
    const homeId = this.home()?.id;
    if (!homeId) return;
    this.homeService.addUser(homeId, user.email, role).subscribe({
      next: () => {
        this.showAddForm.set(false);
        this.errorMessage.set(null);
      },
      error: () => this.errorMessage.set("Impossible d'ajouter cet utilisateur."),
    });
  }

  onUserRemoved(userId: string) {
    const homeId = this.home()?.id;
    if (!homeId) return;
    this.homeService.removeUser(homeId, userId).subscribe();
  }
}
