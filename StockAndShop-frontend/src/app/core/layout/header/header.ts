import { Component, effect, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { filter } from 'rxjs';
import { AuthService } from '../../../features/auth/auth.service';
import { ThemeService } from '../../services/theme.service';

@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './header.html',
})
export class HeaderComponent {
  private authService = inject(AuthService);
  private router = inject(Router);
  readonly themeService = inject(ThemeService);

  isMenuOpen = signal(false);

  private routeChange = toSignal(
    this.router.events.pipe(filter((e) => e instanceof NavigationEnd)),
  );

  constructor() {
    // routeChange() is read purely as a dependency — any navigation triggers this effect and closes the mobile menu
    effect(() => {
      this.routeChange();
      this.isMenuOpen.set(false);
    });
  }

  disconnect() {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
