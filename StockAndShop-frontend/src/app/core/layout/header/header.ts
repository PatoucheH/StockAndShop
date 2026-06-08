import { Component, effect, inject, signal } from '@angular/core';
import { NavigationEnd, Router, RouterLink } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { filter } from 'rxjs';
import { AuthService } from '../../../features/auth/auth.service';

@Component({
  selector: 'app-header',
  imports: [RouterLink],
  templateUrl: './header.html',

})
export class HeaderComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  isMenuOpen = signal(false);

  private routeChange = toSignal(
    this.router.events.pipe(filter((e) => e instanceof NavigationEnd)),
  );

  constructor() {
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
