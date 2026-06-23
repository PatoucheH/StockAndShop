import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './header/header';
import { BottomNavComponent } from './bottom-nav/bottom-nav';

@Component({
  selector: 'app-layout',
  imports: [HeaderComponent, RouterOutlet, BottomNavComponent],
  templateUrl: './layout.html',
})
export class LayoutComponent {}
