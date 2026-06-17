import { Component, input } from '@angular/core';
import { AbstractControl } from '@angular/forms';

@Component({
  selector: 'app-field-error',
  standalone: true,
  imports: [],
  templateUrl: './field-error.html',
})
export class FieldErrorComponent {
  control = input<AbstractControl | null>(null);
  messages = input<Record<string, string>>({});

  getMessages(ctrl: AbstractControl): string[] {
    const errs = ctrl.errors ?? {};
    const custom = this.messages();
    return Object.keys(errs).map(key => {
      if (custom[key]) return custom[key];
      switch (key) {
        case 'required': return 'Ce champ est requis';
        case 'email': return "Format d'email invalide";
        case 'min': return `La valeur minimale est ${errs['min'].min}`;
        case 'max': return `La valeur maximale est ${errs['max'].max}`;
        case 'minlength': return `Minimum ${errs['minlength'].requiredLength} caractères`;
        case 'maxlength': return `Maximum ${errs['maxlength'].requiredLength} caractères`;
        default: return 'Valeur invalide';
      }
    });
  }
}
