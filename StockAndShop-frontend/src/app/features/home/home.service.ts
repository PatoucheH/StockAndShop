import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Home, HomeRequest } from './home.model';
import { finalize, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class HomeService {

  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/home`;

  private _homes = signal<Home[]>([]);
  private _loading = signal<boolean>(true);

  public homes = this._homes.asReadonly();
  public loading = this._loading.asReadonly();

  loadHomes() {
    this._loading.set(true);
    this.http.get<Home[]>(`${this.apiUrl}`).pipe(
      tap(data => this._homes.set(data)),
      finalize(() => this._loading.set(false))
    ).subscribe();
  }

  createNewHome(data: HomeRequest){
    this.http.post<Home>(`${this.apiUrl}`, data).pipe(
      tap(home => this._homes.update(homes => [...homes, home]))
    ).subscribe();
  }

}
