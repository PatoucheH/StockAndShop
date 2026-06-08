import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class UnitConversionService {
  isWeightOrVolume(unity: string): boolean {
    const u = unity.toLowerCase();
    return u === 'grams' || u === 'milliliter';
  }

  getDefaultSubUnit(unity: string): string {
    return unity.toLowerCase() === 'milliliter' ? 'ml' : 'g';
  }

  getSubUnits(unity: string): string[] {
    return unity.toLowerCase() === 'milliliter' ? ['ml', 'L'] : ['g', 'kg'];
  }

  toBaseUnit(quantity: number, subUnit: string): number {
    return subUnit === 'kg' || subUnit === 'L' ? Math.round(quantity * 1000) : quantity;
  }
}
