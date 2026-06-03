import { Component, effect, inject, signal } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { filter } from 'rxjs';
import { AuthService } from '../../../features/auth/auth.service';

@Component({
  selector: 'app-header',
  imports: [],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class HeaderComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  isMenuOpen = signal(false);

  // Convertit les événements de navigation en signal pour l'utiliser dans effect()
  private routeChange = toSignal(
    this.router.events.pipe(filter((e) => e instanceof NavigationEnd)),
  );

  constructor() {
    // Ferme le menu mobile automatiquement à chaque changement de route.
    // Sans cet effect(), le menu restait ouvert après navigation (composant non détruit).
    effect(() => {
      this.routeChange(); // dépendance — se déclenche à chaque NavigationEnd
      this.isMenuOpen.set(false);
    });
  }

  disconnect() {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
