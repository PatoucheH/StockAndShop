import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.patoutech.stockandshop',
  appName: 'Stock And Shop',
  // Sortie du build Angular (configuration "capacitor")
  webDir: 'dist/StockAndShop-frontend/browser',
  server: {
    androidScheme: 'https',
  },
  plugins: {
    // Route les requetes HttpClient par la couche HTTP native (URLSession / OkHttp).
    // -> le cookie httpOnly "refreshToken" est stocke dans le jar natif et persiste
    //    entre les redemarrages de l'app, et CORS est contourne. Aucune reecriture de l'auth.
    CapacitorHttp: {
      enabled: true,
    },
    // On garde le splash affiche jusqu'a ce que l'app soit prete (hide() appele dans main.ts),
    // pour eviter un flash blanc pendant le boot.
    SplashScreen: {
      launchAutoHide: false,
      backgroundColor: '#059669',
    },
  },
};

export default config;
