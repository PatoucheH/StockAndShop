import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthResponse, LoginRequest, RegisterRequest } from '../../core/models/auth.model';
import { environment } from '../../../environments/environment';
import { StorageService } from '../../core/services/storage.service';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private http = inject(HttpClient);
  private storage = inject(StorageService);
  private apiUrl = `${environment.apiUrl}/auth`;

  readonly authVersion = signal(0);

  login(body: LoginRequest) {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, body);
  }

  register(body: RegisterRequest) {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, body);
  }

  saveToken(token: string, email: string) {
    this.storage.set('token', token);
    this.storage.set('userEmail', email);
    this.authVersion.update(v => v + 1);
  }

  getToken() {
    return this.storage.get('token');
  }

  getUserEmail(): string | null {
    return this.storage.get('userEmail');
  }

  logout() {
    this.storage.remove('token');
    this.storage.remove('userEmail');
    this.authVersion.update(v => v + 1);
  }

  isLoggedIn() {
    return !!this.getToken();
  }
}
