import { Component, computed, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HomeService } from '../../../../shared/services/home.service';
import { CentsToEurosPipe } from '../../../../shared/pipes/cents-to-euros.pipe';
import { AddHomeExpenseModalComponent } from './add-home-expense-modal/add-home-expense-modal';
import { ModalRefundComponent } from '../modal-refund/modal-refund';

type ExpenseFilter = 'ALL' | 'EXPENSE' | 'REFUND';

@Component({
  selector: 'app-home-expenses',
  imports: [CentsToEurosPipe, AddHomeExpenseModalComponent, ModalRefundComponent, DatePipe],
  templateUrl: './home-expenses.html',
})
export class HomeExpensesComponent {
  private homeService = inject(HomeService);

  expenses = this.homeService.expense;
  userInHome = this.homeService.users;
  hasMoreExpenses = this.homeService.expenseHasMore;
  expensesLoading = this.homeService.expenseIsLoading;

  modalIsOpen = signal(false);
  refundModalIsOpen = signal(false);

  // Filtre client sur les lignes déjà chargées : Tout / Dépenses / Remboursements
  filter = signal<ExpenseFilter>('ALL');
  filterOptions: { value: ExpenseFilter; label: string }[] = [
    { value: 'ALL', label: 'Tout' },
    { value: 'EXPENSE', label: 'Dépenses' },
    { value: 'REFUND', label: 'Remboursements' },
  ];
  filteredExpenses = computed(() => {
    const f = this.filter();
    const all = this.expenses();
    return f === 'ALL' ? all : all.filter((e) => e.type === f);
  });

  loadMoreExpenses() {
    this.homeService.loadMoreExpenses();
  }
}
