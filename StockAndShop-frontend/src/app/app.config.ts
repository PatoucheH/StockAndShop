import { ApplicationConfig, inject, provideBrowserGlobalErrorListeners, provideAppInitializer, provideZonelessChangeDetection, isDevMode } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { providePrimeNG } from 'primeng/config';
import Aura from '@primeuix/themes/aura';

import { routes } from './app.routes';
import { jwtInterceptor } from './core/interceptors/jwt.interceptor';
import { errorInterceptor } from './core/interceptors/error.interceptor';
import { AuthService } from './features/auth/services/auth.service';
import { provideServiceWorker } from '@angular/service-worker';
import { environment } from '../environments/environment';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZonelessChangeDetection(),
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withInterceptors([errorInterceptor, jwtInterceptor])),
    // Angular applies interceptors in declaration order outbound and in reverse inbound.
    // errorInterceptor therefore handles 401s first on the way back, refreshes the token, and retries —
    // the retry travels the full chain again so jwtInterceptor attaches the new access token.
    // Attempts to restore the session from the httpOnly refresh token cookie before the app renders
    provideAppInitializer(() => {
      const auth = inject(AuthService);
      return auth.tryRestoreSession();
    }),
    providePrimeNG({
      theme: {
        preset: Aura,
        options: {
          darkModeSelector: '.dark',
          cssLayer: { name: 'primeng', order: 'tailwind-base, primeng, tailwind-utilities' },
        },
      },
    }), provideServiceWorker('ngsw-worker.js', {
            // Jamais de SW en natif : en WebView locale il casse le cache/navigation et n'a aucun intérêt.
            enabled: !isDevMode() && !environment.native,
            registrationStrategy: 'registerWhenStable:30000'
          }),
  ],
};
