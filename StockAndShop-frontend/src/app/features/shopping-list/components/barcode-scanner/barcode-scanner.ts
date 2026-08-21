import { AfterViewInit, Component, OnDestroy, output } from '@angular/core';
import { Capacitor } from '@capacitor/core';
import { Html5Qrcode, Html5QrcodeSupportedFormats } from 'html5-qrcode';

/**
 * Scanner de code-barres à deux implémentations :
 *  - NATIF (iOS/Android) : plugin ML Kit (@capacitor-mlkit/barcode-scanning) — décodage fiable,
 *    caméra native affichée derrière la WebView rendue transparente.
 *  - WEB / PWA : html5-qrcode (getUserMedia dans la WebView) — inchangé.
 * Les deux émettent le même {@link barcodeDetected}.
 */
@Component({
  selector: 'app-barcode-scanner',
  imports: [],
  templateUrl: './barcode-scanner.html',
})
export class BarcodeScanner implements AfterViewInit, OnDestroy {
  barcodeDetected = output<string>();
  cancelled = output<void>();

  // Exposé au template pour choisir l'UI (overlay natif vs lecteur web).
  readonly isNative = Capacitor.isNativePlatform();

  private stopped = false;

  // Web
  private scanner?: Html5Qrcode;
  // Natif : handles de listeners à retirer
  private nativeListeners: { remove: () => Promise<void> }[] = [];

  ngAfterViewInit() {
    if (this.isNative) this.startNative();
    else this.startWeb();
  }

  stop() {
    this.doStop().then(() => this.cancelled.emit());
  }

  ngOnDestroy() {
    this.doStop().catch(() => {});
  }

  // ============================ NATIF (ML Kit) ============================ //
  private async startNative() {
    try {
      const { BarcodeScanner, BarcodeFormat } = await import('@capacitor-mlkit/barcode-scanning');

      const { camera } = await BarcodeScanner.requestPermissions();
      if (camera !== 'granted' && camera !== 'limited') {
        this.cancelled.emit();
        return;
      }

      // Rend la WebView transparente : la caméra native s'affiche derrière,
      // seul l'overlay (cadre + bouton) reste visible (voir styles.scss).
      document.body.classList.add('barcode-scanning-active');

      const onScan = async (event: any) => {
        const value = event?.barcodes?.[0]?.rawValue;
        if (!value || this.stopped) return;
        await this.doStop();
        this.barcodeDetected.emit(value);
      };

      this.nativeListeners = [
        await BarcodeScanner.addListener('barcodesScanned', onScan),
      ];

      await BarcodeScanner.startScan({
        formats: [
          BarcodeFormat.Ean13,
          BarcodeFormat.Ean8,
          BarcodeFormat.UpcA,
          BarcodeFormat.UpcE,
          BarcodeFormat.Code128,
        ],
      });
    } catch {
      await this.stopNative();
      this.cancelled.emit();
    }
  }

  private async stopNative() {
    document.body.classList.remove('barcode-scanning-active');
    for (const l of this.nativeListeners) {
      try { await l.remove(); } catch { /* ignore */ }
    }
    this.nativeListeners = [];
    try {
      const { BarcodeScanner } = await import('@capacitor-mlkit/barcode-scanning');
      await BarcodeScanner.stopScan();
    } catch { /* ignore */ }
  }

  // ============================== WEB (html5-qrcode) ============================== //
  private startWeb() {
    this.scanner = new Html5Qrcode('barcode-reader', {
      verbose: false,
      formatsToSupport: [
        Html5QrcodeSupportedFormats.EAN_13,
        Html5QrcodeSupportedFormats.EAN_8,
        Html5QrcodeSupportedFormats.UPC_A,
        Html5QrcodeSupportedFormats.UPC_E,
        Html5QrcodeSupportedFormats.CODE_128,
      ],
    });
    this.scanner
      .start(
        { facingMode: 'environment', advanced: [{ focusMode: 'continuous' }] } as any,
        {
          fps: 10,
          qrbox: (viewfinderWidth: number) => {
            const width = Math.min(320, Math.floor(viewfinderWidth * 0.9));
            return { width, height: Math.floor(width * 0.6) };
          },
        },
        (barcode) => this.handleWebDetection(barcode),
        () => {},
      )
      .catch(() => this.cancelled.emit());
  }

  private async stopWeb() {
    if (!this.scanner) return;
    try {
      await this.scanner.stop();
      this.scanner.clear();
    } catch { /* ignore */ }
  }

  private handleWebDetection(barcode: string) {
    if (this.stopped) return;
    this.doStop().then(() => this.barcodeDetected.emit(barcode));
  }

  // ============================== COMMUN ============================== //
  private doStop(): Promise<void> {
    if (this.stopped) return Promise.resolve();
    this.stopped = true;
    return this.isNative ? this.stopNative() : this.stopWeb();
  }
}
