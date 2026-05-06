import { Component, inject } from '@angular/core';
import { AuthService } from '../../../features/auth/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  imports: [],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class HeaderComponent {

  private authService = inject(AuthService);
  private router = inject(Router);


  disconnect(){
    this.authService.logout();
    this.router.navigate(['/auth/login']);
  }
}
