import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Capacitor } from '@capacitor/core';
import { AuthService } from '../auth/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { ConfirmModalComponent } from '../../shared/components/confirm-modal/confirm-modal';

@Component({
  selector: 'app-profile',
  imports: [ConfirmModalComponent],
  templateUrl: './profile.html',
})
export class ProfileComponent {
  private authService = inject(AuthService);
  private toast = inject(ToastService);
  private router = inject(Router);

  username = signal(this.authService.getDisplayName() ?? '');
  email = this.authService.getUserEmail() ?? '';

  savingUsername = signal(false);
  deleting = signal(false);
  confirmDeleteOpen = signal(false);

  currentPassword = signal('');
  newPassword = signal('');
  confirmPassword = signal('');
  savingPassword = signal(false);
  showCurrentPassword = signal(false);
  showNewPassword = signal(false);
  showConfirmPassword = signal(false);

  saveUsername() {
    const name = this.username().trim();
    if (!name) {
      this.toast.error("Le nom d'utilisateur ne peut pas être vide");
      return;
    }
    this.savingUsername.set(true);
    this.authService.updateUsername(name).subscribe({
      next: () => {
        this.savingUsername.set(false);
        this.toast.success('Nom mis à jour');
      },
      error: (err) => {
        this.savingUsername.set(false);
        this.toast.error(err?.error?.message ?? 'Impossible de mettre à jour le nom');
      },
    });
  }

  changePassword() {
    const current = this.currentPassword();
    const next = this.newPassword();
    const confirm = this.confirmPassword();
    if (!current || !next) {
      this.toast.error('Remplissez tous les champs');
      return;
    }
    if (next.length < 8) {
      this.toast.error('Le nouveau mot de passe doit contenir au moins 8 caractères');
      return;
    }
    if (next !== confirm) {
      this.toast.error('Les deux mots de passe ne correspondent pas');
      return;
    }
    this.savingPassword.set(true);
    this.authService.changePassword(current, next).subscribe({
      next: () => {
        this.savingPassword.set(false);
        this.currentPassword.set('');
        this.newPassword.set('');
        this.confirmPassword.set('');
        this.toast.success('Mot de passe modifié');
      },
      error: (err) => {
        this.savingPassword.set(false);
        this.toast.error(err?.error?.message ?? 'Impossible de modifier le mot de passe');
      },
    });
  }

  deleteAccount() {
    this.deleting.set(true);
    this.authService.deleteAccount().subscribe({
      next: () => {
        this.deleting.set(false);
        this.confirmDeleteOpen.set(false);
        this.toast.success('Compte supprimé');
        this.router.navigate(['/auth/login']);
      },
      error: (err) => {
        this.deleting.set(false);
        this.confirmDeleteOpen.set(false);
        this.toast.error(err?.error?.message ?? 'Impossible de supprimer le compte');
      },
    });
  }

  logout() {
    this.authService.logout();
  }

  // En natif, on ouvre les liens externes dans un onglet in-app (Custom Tab / SFSafariViewController)
  // au lieu de laisser Android les router vers la PWA installée ou une autre app.
  // Sur le web, on ne fait rien : le lien garde son comportement target="_blank".
  async openExternal(event: Event, url: string) {
    if (!Capacitor.isNativePlatform()) return;
    event.preventDefault();
    const { Browser } = await import('@capacitor/browser');
    await Browser.open({ url });
  }
}
