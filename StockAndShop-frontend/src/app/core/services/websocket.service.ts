import { effect, inject, Injectable, OnDestroy } from '@angular/core';
import { AuthService } from '../../features/auth/services/auth.service';
import { Client, IMessage, StompSubscription } from '@stomp/stompjs';
import { environment } from '../../../environments/environment';
import { Observable } from 'rxjs';

/**
 * Singleton STOMP client for the whole application.
 *
 * Activates when the user logs in, deactivates on logout, and transparently
 * re-subscribes to all active topics after a reconnect.
 */
@Injectable({ providedIn: 'root' })
export class WebSocketService implements OnDestroy {
  private authService = inject(AuthService);

  // Two separate maps because @stomp/stompjs does NOT restore subscriptions after a reconnect.
  // activeTopics holds the registered callbacks and survives reconnects;
  // liveSubscriptions holds the actual STOMP handles and is rebuilt on every new session.
  private activeTopics = new Map<string, Set<(msg: IMessage) => void>>();
  private liveSubscriptions = new Map<string, StompSubscription>();

  private client = new Client({
    brokerURL:
      environment.wsUrl || `${location.protocol === 'https:' ? 'wss' : 'ws'}://${location.host}/ws`,
    reconnectDelay: 5000,
    heartbeatIncoming: 10000,
    heartbeatOutgoing: 10000,
    onConnect: () => this.resubscribeAll(),
  });

  constructor() {
    effect(() => {
      const token = this.authService.getToken();
      // Read authVersion to react to all auth state changes, not just token value changes.
      this.authService.authVersion();

      if (token) {
        // connectHeaders are only sent on the next STOMP CONNECT frame.
        // A token refresh must not force a reconnect of an already healthy session.
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
    this.liveSubscriptions.set(
      topic,
      this.client.subscribe(topic, (msg) => {
        this.activeTopics.get(topic)?.forEach((cb) => cb(msg));
      }),
    );
  }

  /**
   * Returns a cold Observable that subscribes to {@code topic} on the STOMP broker.
   * The STOMP subscription is created lazily and torn down when all observers unsubscribe.
   */
  subscribe(topic: string): Observable<IMessage> {
    return new Observable((observer) => {
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
