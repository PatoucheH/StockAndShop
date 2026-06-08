import { Pipe, PipeTransform } from '@angular/core';

const UNIT_LABELS: Record<string, string> = {
  grams: 'g',
  milliliter: 'ml',
  piece: 'pièce',
  jar: 'pot',
  tub: 'tube',
  bottle: 'bouteille',
  packet: 'paquet',
  box: 'boite',
};

@Pipe({ name: 'smartUnit', standalone: true })
export class SmartUnitPipe implements PipeTransform {
  transform(quantity: number, unity: string): string {
    const u = unity.toLowerCase();
    if (u === 'grams' && quantity >= 1000) {
      const kg = quantity / 1000;
      return `${Number.isInteger(kg) ? kg : +kg.toFixed(2)} kg`;
    }
    if (u === 'milliliter' && quantity >= 1000) {
      const l = quantity / 1000;
      return `${Number.isInteger(l) ? l : +l.toFixed(2)} L`;
    }
    return `${quantity} ${UNIT_LABELS[u] ?? u}`;
  }
}
