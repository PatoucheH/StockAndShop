import { Pipe, PipeTransform } from '@angular/core';

export const UNITY_LABELS: Record<string, string> = {
  MILLILITER: 'Millilitres',
  GRAMS: 'Grammes',
  PIECE: 'Pièce',
  JAR: 'Pot',
  TUB: 'Tube',
  BOTTLE: 'Bouteille',
  PACKET: 'Paquet',
  BOX: 'Boite',
};

@Pipe({ name: 'unityLabel', standalone: true })
export class UnityLabelPipe implements PipeTransform {
  transform(unity: string): string {
    return UNITY_LABELS[unity.toUpperCase()] ?? unity;
  }
}
