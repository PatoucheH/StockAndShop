import { Component, computed, inject, signal } from '@angular/core';
import { HomeService } from '../../../../shared/services/home.service';
import { AuthService } from '../../../auth/services/auth.service';
import { UserSearchResult } from '../../../../shared/models/user.models';
import { UserSearchInputComponent } from './user-search-input/user-search-input';
import { HomeMemberListComponent } from './home-member-list/home-member-list';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-manage-users-home',
  imports: [UserSearchInputComponent, HomeMemberListComponent],
  templateUrl: './manage-users-home.html',

})
export class ManageUsersHomeComponent {
  private homeService = inject(HomeService);
  private authService = inject(AuthService);
  private toast = inject(ToastService);

  users = this.homeService.users;
  home = this.homeService.selectedHome;
  isOwner = computed(() => this.home()?.ownerEmail === this.authService.getUserEmail());
  existingEmails = computed(() => this.users().map(u => u.email));

  showAddForm = signal(false);
  errorMessage = signal<string | null>(null);

  toggleAddForm() {
    this.showAddForm.update(v => !v);
    if (!this.showAddForm()) this.errorMessage.set(null);
  }

  onUserSubmitted({ user, role }: { user: UserSearchResult; role: 'USER' | 'VIEWER' }) {
    this.homeService.addUser(user.email, role).subscribe({
      next: () => {
        this.showAddForm.set(false);
        this.errorMessage.set(null);
        this.toast.success('Utilisateur ajouté');
      },
      error: () => this.errorMessage.set("Impossible d'ajouter cet utilisateur."),
    });
  }

  onUserRemoved(userId: string) {
    this.homeService.removeUser(userId).subscribe({
      next: () => this.toast.success('Membre retiré'),
      error: () => this.toast.error('Impossible de retirer ce membre'),
    });
  }
}
