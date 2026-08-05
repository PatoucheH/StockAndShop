import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthResponse, LoginRequest, RegisterRequest } from '../../../core/models/auth.models';
import { environment } from '../../../../environments/environment';
import { catchError, finalize, map, Observable, of, shareReplay, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private http = inject(HttpClient);
  private router = inject(Router);
  private apiUrl = `${environment.apiUrl}/auth`;

  private readonly _accessToken = signal<string | null>(null);

  // Monotonically increasing counter used as a reactive dependency — increment to force all auth-dependent signals to re-evaluate
  readonly authVersion = signal(0);

  // Like authVersion but only bumped on an actual login/logout, never on a silent token refresh —
  // use this for state that must survive a background refresh (e.g. the selected shopping list / its WS subscription)
  readonly identityVersion = signal(0);

  login(body: LoginRequest) {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, body, { withCredentials: true });
  }

  register(body: RegisterRequest) {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, body, { withCredentials: true });
  }

  saveToken(token: string, email: string, displayName: string) {
    this._accessToken.set(token);
    localStorage.setItem('userEmail', email);
    localStorage.setItem('displayName', displayName);
    this.authVersion.update(v => v + 1);
    this.identityVersion.update(v => v + 1);
  }

  getToken() {
    return this._accessToken();
  }

  getUserEmail(): string | null {
    return localStorage.getItem('userEmail');
  }

  getDisplayName(): string | null {
    return localStorage.getItem('displayName');
  }

  isLoggedIn(): boolean {
    return this._accessToken() !== null;
  }

  clearSession(){
    this._accessToken.set(null);
    localStorage.removeItem('userEmail');
    localStorage.removeItem('displayName');
    this.authVersion.update(v => v + 1);
    this.identityVersion.update(v => v + 1);
  }

  logout() {
    this.http.post(`${this.apiUrl}/logout`, {}, { withCredentials: true }).subscribe();
    this.clearSession();
    this.router.navigate(['/auth/login']);
  }

  updateUsername(username: string) {
    return this.http
      .put(`${environment.apiUrl}/user/me/username`, { username })
      .pipe(tap(() => localStorage.setItem('displayName', username)));
  }

  changePassword(currentPassword: string, newPassword: string) {
    return this.http.put(`${environment.apiUrl}/user/me/password`, { currentPassword, newPassword });
  }

  // Anonymizes the account server-side, then clears the local session
  deleteAccount() {
    return this.http
      .delete(`${environment.apiUrl}/user/me`)
      .pipe(tap(() => this.clearSession()));
  }

  // Several requests can 401 at once when the access token expires; without this guard
  // each one would fire its own /auth/refresh call and race on the single-use refresh cookie.
  private refreshInFlight$: Observable<boolean> | null = null;

  tryRestoreSession(): Observable<boolean> {
    if (this.refreshInFlight$) {
      return this.refreshInFlight$;
    }

    this.refreshInFlight$ = this.http.post<{ accessToken: string }>(
      `${this.apiUrl}/refresh`, {}, { withCredentials: true }
    ).pipe(
      tap(res => {
        this._accessToken.set(res.accessToken);
        this.authVersion.update(v => v + 1);
      }),
      map(() => true),
      catchError(() => of(false)),
      finalize(() => { this.refreshInFlight$ = null; }),
      shareReplay(1)
    );

    return this.refreshInFlight$;
  }
}
