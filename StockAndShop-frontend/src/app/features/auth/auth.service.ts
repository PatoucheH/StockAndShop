import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthResponse, LoginRequest, RegisterRequest } from '../../core/models/auth.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/auth`;

  readonly authVersion = signal(0);

  login(body: LoginRequest) {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, body);
  }

  register(body: RegisterRequest) {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, body);
  }

  saveToken(token: string, email: string) {
    localStorage.setItem('token', token);
    localStorage.setItem('userEmail', email);
    this.authVersion.update(v => v + 1);
  }

  getToken() {
    return localStorage.getItem('token');
  }

  getUserEmail(): string | null {
    return localStorage.getItem('userEmail');
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('userEmail');
    this.authVersion.update(v => v + 1);
  }

  isLoggedIn() {
    return !!this.getToken();
  }
}
