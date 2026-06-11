import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthResponse, LoginRequest, RegisterRequest } from '../../core/models/auth.models';
import { environment } from '../../../environments/environment';
import { catchError, map, Observable, of, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/auth`;

  private readonly _accessToken = signal<string | null>(null);

  readonly authVersion = signal(0);

  login(body: LoginRequest) {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, body, { withCredentials: true });
  }

  register(body: RegisterRequest) {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, body, { withCredentials: true });
  }

  saveToken(token: string, email: string) {
    this._accessToken.set(token);
    localStorage.setItem('userEmail', email);
    this.authVersion.update(v => v + 1);
  }

  getToken() {
    return this._accessToken();
  }

  getUserEmail(): string | null {
    return localStorage.getItem('userEmail');
  }

  isLoggedIn(): boolean {
    return this._accessToken() !== null;
  }

  clearSession(){
    this._accessToken.set(null);
    localStorage.removeItem('userEmail');
    this.authVersion.update(v => v + 1);
  }

  logout() {
    this.http.post(`${this.apiUrl}/logout`, {}, { withCredentials: true }).subscribe();
    this.clearSession();
  }

  tryRestoreSession(): Observable<boolean> {
    return this.http.post<{ accessToken: string }>(
      `${this.apiUrl}/refresh`, {}, { withCredentials: true }
    ).pipe(
      tap(res => {
        this._accessToken.set(res.accessToken);
        this.authVersion.update(v => v + 1);
      }),
      map(() => true),
      catchError(() => of(false))
    );
  }
}
