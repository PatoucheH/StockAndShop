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
        @if (showHelp()) {
          <div class="pwa-help">
            @if (isIos()) {
              Pour installer : appuie sur <strong>Partager</strong> puis
              <strong>« Sur l'écran d'accueil »</strong>.
            } @else {
              Pour installer : ouvre le menu <strong>⋮</strong> du navigateur puis
              <strong>« Installer l'application »</strong> (ou « Ajouter à l'écran d'accueil »).
            }
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
  readonly showHelp = signal(false);
  readonly isIos = signal(false);
  private deferredPrompt: BeforeInstallPromptEvent | null = null;

  constructor() {
    afterNextRender(() => {
      if (!isPlatformBrowser(this.platformId)) return;

      const standalone = window.matchMedia('(display-mode: standalone)').matches
        || (window.navigator as any).standalone === true;
      if (standalone) return; // running as an installed app → no button needed

      const ua = window.navigator.userAgent.toLowerCase();
      this.isIos.set(/iphone|ipad|ipod/.test(ua) && !/crios|fxios|edgios/.test(ua));

      // Always offer installation when the app is not installed. If Chrome fires
      // beforeinstallprompt we can trigger the native dialog; otherwise (iOS, or after
      // a previous install+uninstall where Chrome stops firing the event) the button
      // falls back to manual instructions.
      this.visible.set(true);

      window.addEventListener('beforeinstallprompt', (e: Event) => {
        e.preventDefault();
        this.deferredPrompt = e as BeforeInstallPromptEvent;
      });
      window.addEventListener('appinstalled', () => {
        this.visible.set(false);
        this.deferredPrompt = null;
      });
    });
  }

  async onClick() {
    // Native install dialog available (Chrome/Edge/Android) → use it
    if (this.deferredPrompt) {
      await this.deferredPrompt.prompt();
      await this.deferredPrompt.userChoice;
      this.deferredPrompt = null;
      this.visible.set(false);
      return;
    }
    // No native prompt (iOS, or Chrome after a previous uninstall) → show manual steps
    this.showHelp.update(v => !v);
  }
}
