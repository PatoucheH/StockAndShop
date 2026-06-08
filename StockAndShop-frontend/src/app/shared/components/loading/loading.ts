import { Component, input } from '@angular/core';

@Component({
  selector: 'app-loading',
  imports: [],
  templateUrl: './loading.html',
})
export class LoadingComponent {
  message = input('Chargement...');
}
