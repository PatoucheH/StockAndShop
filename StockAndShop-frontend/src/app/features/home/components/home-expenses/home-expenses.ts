import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { HomeService } from '../../../../shared/services/home.service';
import { CentsToEurosPipe } from '../../../../shared/pipes/cents-to-euros.pipe';
import { AddHomeExpenseModalComponent } from './add-home-expense-modal/add-home-expense-modal';

@Component({
  selector: 'app-home-expenses',
  imports: [CentsToEurosPipe, AddHomeExpenseModalComponent, DatePipe],
  templateUrl: './home-expenses.html',
})
export class HomeExpensesComponent {
  private homeService = inject(HomeService);

  expenses = this.homeService.expense;
  userInHome = this.homeService.users;
  hasMoreExpenses = this.homeService.expenseHasMore;
  expensesLoading = this.homeService.expenseIsLoading;

  modalIsOpen = signal(false);

  loadMoreExpenses() {
    this.homeService.loadMoreExpenses();
  }
}
