import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';
import { Capacitor } from '@capacitor/core';

bootstrapApplication(App, appConfig)
  .then(() => initNative())
  .catch((err) => console.error(err));

// Réglages spécifiques à l'app native (no-op sur le web grâce au garde isNativePlatform).
// Imports dynamiques : les plugins Capacitor ne sont pas embarqués dans le bundle web.
async function initNative(): Promise<void> {
  if (!Capacitor.isNativePlatform()) return;

  try {
    const { StatusBar, Style } = await import('@capacitor/status-bar');
    // Style.Dark = texte clair (blanc) — lisible sur l'en-tête vert foncé.
    await StatusBar.setStyle({ style: Style.Dark });
    if (Capacitor.getPlatform() === 'android') {
      // La status bar ne recouvre pas la WebView : le contenu démarre dessous,
      // donc pas de contenu masqué par l'encoche/barre d'état.
      await StatusBar.setOverlaysWebView({ overlay: false });
      await StatusBar.setBackgroundColor({ color: '#064e3b' });
    }
  } catch {
    /* plugin indisponible : on ignore, l'app reste fonctionnelle */
  }

  try {
    const { SplashScreen } = await import('@capacitor/splash-screen');
    await SplashScreen.hide();
  } catch {
    /* idem */
  }
}
