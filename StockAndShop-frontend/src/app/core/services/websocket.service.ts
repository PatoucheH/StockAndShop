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
      // authVersion is read to make this effect reactive to all auth changes, not just token value changes
      this.authService.authVersion();

      if (token) {
        this.client.connectHeaders = { Authorization: `Bearer ${token}` };
        // Reconnect with updated headers when the token changes
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
      // @stomp/stompjs queues subscriptions if not yet connected and re-establishes them automatically after reconnection
      const sub = this.client.subscribe(topic, msg => observer.next(msg));
      return () => sub.unsubscribe();
    });
  }

  ngOnDestroy(): void {
    this.client.deactivate();
  }
}
