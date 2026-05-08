import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-confirm-modal',
  imports: [],
  templateUrl: './confirm-modal.html',
})
export class ConfirmModalComponent {
  message = input.required<string>();
  confirmLabel = input('Confirmer');
  cancelLabel = input('Annuler');

  confirmed = output<void>();
  cancelled = output<void>();
}
