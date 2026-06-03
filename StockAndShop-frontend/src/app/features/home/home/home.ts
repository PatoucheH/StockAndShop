import { Component, inject, signal } from '@angular/core';
import { HomeService } from '../home.service';
import { AddHomeComponent } from '../add-home/add-home';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  imports: [AddHomeComponent, RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class HomeComponent {
  homeService = inject(HomeService);

  homes = this.homeService.homes;

  modalIsOpen = signal(false);

  openModal() {
    this.modalIsOpen.set(true);
  }
}
