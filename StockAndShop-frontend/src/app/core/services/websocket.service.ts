import { effect, inject, Injectable, OnDestroy } from '@angular/core';
import { AuthService } from '../../features/auth/auth.service';
import { Client, IMessage } from '@stomp/stompjs';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class WebSocketService implements OnDestroy {
  private authService = inject(AuthService);

  private client = new Client({
    brokerURL: environment.wsUrl,
    reconnectDelay: 5000,
  });

  constructor() {
    effect(() => {
      const token = this.authService.getToken();
      this.authService.authVersion(); // rend l'effect réactif aux changements de session

      if (token) {
        this.client.connectHeaders = { Authorization: `Bearer ${token}` };
        if (this.client.active) {
          this.client.deactivate().then(() => this.client.activate());
        } else {
          this.client.activate();
        }
      } else {
        this.client.deactivate();
      }
    });
  }

  subscribe(topic: string): Observable<IMessage> {
    return new Observable(observer => {
      // @stomp/stompjs met les subscriptions en queue si pas encore connecté
      // et les ré-établit automatiquement après reconnexion
      const sub = this.client.subscribe(topic, msg => observer.next(msg));
      return () => sub.unsubscribe();
    });
  }

  ngOnDestroy(): void {
    this.client.deactivate();
  }
}
