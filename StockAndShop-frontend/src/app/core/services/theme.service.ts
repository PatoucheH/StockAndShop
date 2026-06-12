import { effect, inject, Injectable, signal } from '@angular/core';
import { DOCUMENT } from '@angular/common';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  private doc = inject(DOCUMENT);

  readonly isDark = signal(this.resolveInitialTheme());

  constructor() {
    effect(() => {
      this.doc.documentElement.classList.toggle('dark', this.isDark());
      localStorage.setItem('theme', this.isDark() ? 'dark' : 'light');
    });
  }

  toggle() {
    this.isDark.update(v => !v);
  }

  private resolveInitialTheme(): boolean {
    const stored = localStorage.getItem('theme');
    if (stored) return stored === 'dark';
    // Falls back to the OS dark-mode preference when no stored theme exists
    return this.doc.defaultView?.matchMedia('(prefers-color-scheme: dark)').matches ?? false;
  }
}
