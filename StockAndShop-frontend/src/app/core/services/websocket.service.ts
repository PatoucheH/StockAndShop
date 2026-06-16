import { effect, inject, Injectable, OnDestroy } from '@angular/core';
import { AuthService } from '../../features/auth/auth.service';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class WebSocketService implements OnDestroy {
  private authService = inject(AuthService);

  // @stomp/stompjs does NOT remember subscriptions across a reconnect — each reconnect gets a
  // brand new STOMP session, so every active topic must be re-subscribed by hand in onConnect.
  private activeTopics = new Map<string, Set<(msg: IMessage) => void>>();
  private liveSubscriptions = new Map<string, StompSubscription>();

  private client = new Client({
    brokerURL: environment.wsUrl,
    reconnectDelay: 5000,
    heartbeatIncoming: 10000,
    heartbeatOutgoing: 10000,
    onConnect: () => this.resubscribeAll(),
  });

  constructor() {
    effect(() => {
      const token = this.authService.getToken();
      // authVersion is read to make this effect reactive to all auth changes, not just token value changes
      this.authService.authVersion();

      if (token) {
        // Used on the next CONNECT frame only (initial connect or a future auto-reconnect) —
        // the JWT is never re-checked on an already-open session, so refreshing the token
        // must not force a disconnect/reconnect of an otherwise healthy WebSocket.
        this.client.connectHeaders = { Authorization: `Bearer ${token}` };
        if (!this.client.active) {
          this.client.activate();
        }
      } else {
        this.client.deactivate();
      }
    });
  }

  private resubscribeAll() {
    for (const topic of this.activeTopics.keys()) {
      this.subscribeTopic(topic);
    }
  }

  private subscribeTopic(topic: string) {
    this.liveSubscriptions.set(topic, this.client.subscribe(topic, msg => {
      this.activeTopics.get(topic)?.forEach(cb => cb(msg));
    }));
  }

  subscribe(topic: string): Observable<IMessage> {
    return new Observable(observer => {
      const callback = (msg: IMessage) => observer.next(msg);
      let callbacks = this.activeTopics.get(topic);
      if (!callbacks) {
        callbacks = new Set();
        this.activeTopics.set(topic, callbacks);
      }
      callbacks.add(callback);

      if (this.client.connected && !this.liveSubscriptions.has(topic)) {
        this.subscribeTopic(topic);
      }

      return () => {
        callbacks!.delete(callback);
        if (callbacks!.size === 0) {
          this.activeTopics.delete(topic);
          this.liveSubscriptions.get(topic)?.unsubscribe();
          this.liveSubscriptions.delete(topic);
        }
      };
    });
  }

  ngOnDestroy(): void {
    this.client.deactivate();
  }
}
