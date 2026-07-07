import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'centsToEuros', standalone: true })
export class CentsToEurosPipe implements PipeTransform {
  private formatter = new Intl.NumberFormat('fr-BE', { style: 'currency', currency: 'EUR' });

  transform(cents: number): string {
    return this.formatter.format(cents / 100);
  }
}
