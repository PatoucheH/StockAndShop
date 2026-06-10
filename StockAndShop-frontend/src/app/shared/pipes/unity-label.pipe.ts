import { Pipe, PipeTransform } from '@angular/core';
import { UNITS } from './units.const';

@Pipe({ name: 'unityLabel', standalone: true })
export class UnityLabelPipe implements PipeTransform {
  transform(unity: string): string {
    return UNITS[unity.toLowerCase()]?.long ?? unity;
  }
}
