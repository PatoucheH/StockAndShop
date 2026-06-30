import { AfterViewInit, Component, OnDestroy, output } from '@angular/core';
import { Html5Qrcode, Html5QrcodeSupportedFormats } from 'html5-qrcode';

@Component({
  selector: 'app-barcode-scanner',
  imports: [],
  templateUrl: './barcode-scanner.html',
})
export class BarcodeScanner implements AfterViewInit, OnDestroy {
  barcodeDetected = output<string>();
  cancelled = output<void>();

  private scanner?: Html5Qrcode;
  private stopped = false;

  ngAfterViewInit() {
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
        { facingMode: 'environment' },
        { fps: 15, qrbox: { width: 320, height: 120 } },
        (barcode) => this.handleDetection(barcode),
        () => {},
      )
      .catch(() => this.cancelled.emit());
  }

  stop() {
    this.doStop().then(() => this.cancelled.emit());
  }

  ngOnDestroy() {
    this.doStop().catch(() => {});
  }

  private handleDetection(barcode: string) {
    if (this.stopped) return;
    this.doStop().then(() => this.barcodeDetected.emit(barcode));
  }

  private doStop(): Promise<void> {
    if (this.stopped || !this.scanner) return Promise.resolve();
    this.stopped = true;
    return this.scanner.stop()
      .then(() => { this.scanner!.clear(); })
      .catch(() => {});
  }
}
