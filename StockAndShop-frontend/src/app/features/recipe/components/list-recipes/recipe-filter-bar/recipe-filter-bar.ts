import { Component, inject, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { toSignal, toObservable } from '@angular/core/rxjs-interop';
import { debounceTime, distinctUntilChanged, switchMap, of } from 'rxjs';
import { ProductService } from '../../../../../shared/services/product.service';

@Component({
  selector: 'app-recipe-filter-bar',
  imports: [FormsModule],
  templateUrl: './recipe-filter-bar.html',
})
export class RecipeFilterBarComponent {
  private productService = inject(ProductService);

  activeFilters = input<string[]>([]);
  resultCount = input<number>(0);
  filterAdded = output<string>();
  filterRemoved = output<string>();
  filtersCleared = output<void>();
  nameFilterChanged = output<string>();

  filterInput = signal('');
  filterNameInput = signal('');

  filteredSuggestions = toSignal(
    toObservable(this.filterInput).pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(term => (term && term.length >= 2)
        ? this.productService.searchByName(term)
        : of([])
      )
    ),
    { initialValue: [] }
  );

  onNameFilterChange(value: string) {
    this.filterNameInput.set(value);
    this.nameFilterChanged.emit(value);
  }

  addFilter() {
    const val = this.filterInput().trim().toLowerCase();
    if (!val || this.activeFilters().includes(val)) return;
    this.filterAdded.emit(val);
    this.filterInput.set('');
  }

  removeFilter(filter: string) {
    this.filterRemoved.emit(filter);
  }

  clearFilters() {
    this.filtersCleared.emit();
    this.filterInput.set('');
  }
}
