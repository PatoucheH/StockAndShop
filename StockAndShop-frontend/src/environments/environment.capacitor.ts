// Build natif (app iOS/Android via Capacitor).
// En WebView l'app est servie en local (https://localhost), donc l'API et le WebSocket
// DOIVENT être en URL absolue vers le serveur de prod, pas en chemin relatif.
export const environment = {
  production: true,
  apiUrl: 'https://stockandshop.patoutech.com/api',
  wsUrl: 'wss://stockandshop.patoutech.com/ws',
  native: true,
};
