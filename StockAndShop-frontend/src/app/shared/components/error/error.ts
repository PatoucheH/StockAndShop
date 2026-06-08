import { Component, input } from '@angular/core';

@Component({
  selector: 'app-error',
  imports: [],
  templateUrl: './error.html',
})
export class ErrorComponent {
  message = input('Une erreur est survenue.');
}
