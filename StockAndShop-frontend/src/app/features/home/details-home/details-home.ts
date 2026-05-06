import { Component, inject } from '@angular/core';
import { HomeService } from '../home.service';

@Component({
  selector: 'app-details-home',
  imports: [],
  templateUrl: './details-home.html',
  styleUrl: './details-home.scss',
})
export class DetailsHome {

  homeService = inject(HomeService);




}
