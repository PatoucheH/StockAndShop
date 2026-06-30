import { Component, computed, input } from '@angular/core';
import { DecimalPipe } from '@angular/common';

@Component({
  selector: 'app-star-rating',
  imports: [DecimalPipe],
  template: `
    @if (stars()) {
      <div class="flex items-center" [class]="gapClass()">
        @for (filled of stars()!; track $index) {
          <svg [class]="sizeClass() + ' ' + (filled ? 'text-amber-400' : emptyClass())"
               fill="currentColor" viewBox="0 0 24 24">
            <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
          </svg>
        }
        @if (showScore()) {
          <span [class]="labelClass()">{{ score() | number:'1.1-1' }}</span>
        }
      </div>
    }
  `,
})
export class StarRatingComponent {
  score = input.required<number | null>();
  size = input<'sm' | 'md'>('md');
  showScore = input(false);
  variant = input<'default' | 'dark'>('default');

  stars = computed(() => {
    const s = this.score();
    if (s === null) return null;
    return Array.from({ length: 5 }, (_, i) => i < Math.round(s));
  });

  sizeClass = computed(() => this.size() === 'sm' ? 'w-3.5 h-3.5' : 'w-4 h-4');
  gapClass = computed(() => this.size() === 'sm' ? 'gap-0.5' : 'gap-1');
  emptyClass = computed(() => this.variant() === 'dark' ? 'text-emerald-700' : 'text-gray-300 dark:text-gray-600');
  labelClass = computed(() => this.variant() === 'dark'
    ? 'ml-0.5 text-xs text-emerald-200'
    : 'text-sm font-medium text-gray-600 dark:text-gray-400 ml-0.5');
}
