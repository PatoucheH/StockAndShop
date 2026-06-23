import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../features/auth/auth.service';

@Component({
  selector: 'app-bottom-nav',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './bottom-nav.html',
})
export class BottomNavComponent {
  private authService = inject(AuthService);
  private router = inject(Router);

  disconnect() {
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
