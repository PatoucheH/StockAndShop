import { Component, signal, inject, PLATFORM_ID, afterNextRender } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

interface BeforeInstallPromptEvent extends Event {
  prompt: () => Promise<void>;
  userChoice: Promise<{ outcome: 'accepted' | 'dismissed' }>;
}

@Component({
  selector: 'app-pwa-install',
  standalone: true,
  template: `
    @if (visible()) {
      <div class="pwa-install">
        @if (iosHelp()) {
          <div class="pwa-help">
            Pour installer : appuie sur <strong>Partager</strong>, puis
            <strong>« Sur l'écran d'accueil »</strong>.
          </div>
        }
        <button class="pwa-btn" (click)="onClick()">📲 Installer l'application</button>
      </div>
    }
  `,
  styles: [`
    .pwa-install { position: fixed; bottom: 1rem; right: 1rem; z-index: 1000;
      display: flex; flex-direction: column; align-items: flex-end; gap: .5rem; }
    .pwa-btn { background: #059669; color: #fff; border: none; padding: .7rem 1.1rem;
      border-radius: 9999px; font-weight: 600; font-family: inherit;
      box-shadow: 0 4px 14px rgba(0,0,0,.3); cursor: pointer; }
    .pwa-btn:hover { background: #047857; }
    .pwa-help { background: #0f1729; color: #fff; padding: .6rem .8rem; border-radius: .6rem;
      font-size: .85rem; max-width: 15rem; box-shadow: 0 4px 14px rgba(0,0,0,.3); }
  `],
})
export class PwaInstallComponent {
  private platformId = inject(PLATFORM_ID);
  readonly visible = signal(false);
  readonly iosHelp = signal(false);
  private isIos = false;
  private deferredPrompt: BeforeInstallPromptEvent | null = null;

  constructor() {
    afterNextRender(() => {
      if (!isPlatformBrowser(this.platformId)) return;
      const standalone = window.matchMedia('(display-mode: standalone)').matches
        || (window.navigator as any).standalone === true;
      if (standalone) return;

      const ua = window.navigator.userAgent.toLowerCase();
      const ios = /iphone|ipad|ipod/.test(ua);
      const isSafari = ios && !/crios|fxios|edgios/.test(ua);
      if (ios && isSafari) { this.isIos = true; this.visible.set(true); }

      window.addEventListener('beforeinstallprompt', (e: Event) => {
        e.preventDefault();
        this.deferredPrompt = e as BeforeInstallPromptEvent;
        this.visible.set(true);
      });
      window.addEventListener('appinstalled', () => {
        this.visible.set(false);
        this.deferredPrompt = null;
      });
    });
  }

  async onClick() {
    if (this.isIos) { this.iosHelp.update(v => !v); return; }
    if (!this.deferredPrompt) return;
    await this.deferredPrompt.prompt();
    await this.deferredPrompt.userChoice;
    this.deferredPrompt = null;
    this.visible.set(false);
  }
}
