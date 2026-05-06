import { Component, inject, signal } from '@angular/core';
import { HomeService } from './home.service';
import { AddHomeComponent } from './add-home/add-home';

@Component({
  selector: 'app-home',
  imports: [AddHomeComponent],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class HomeComponent {

  homeService = inject(HomeService);

  homes = this.homeService.homes;

  modalIsOpen = signal(false);

  ngOnInit(){
    this.homeService.loadHomes();
  }

  openModal(){
    this.modalIsOpen.set(true);
  }
}
