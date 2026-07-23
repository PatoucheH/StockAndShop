import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ToastComponent } from './core/components/toast/toast.component';
import { PwaInstallComponent } from './core/components/pwa-install/pwa-install';
import { ThemeService } from './core/services/theme.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ToastComponent, PwaInstallComponent],
  templateUrl: './app.html',
})
export class App {
  readonly themeService = inject(ThemeService);
}
