import { Component, computed, inject, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../../shared/services/product.service';

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

  filterInput = signal('');

  filteredSuggestions = computed(() => {
    const search = this.filterInput().toLowerCase();
    if (!search) return [];
    return this.productService.allProducts().filter((p) =>
      p.name.toLowerCase().includes(search),
    );
  });

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
