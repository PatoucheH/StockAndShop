import { Component, computed, effect, inject, output, signal } from '@angular/core';
import { HomeService } from '../../../../../shared/services/home.service';
import { HomeExpenseRequest } from '../../../models/home-expense.model';
import { FormField, form, required, min, validate } from '@angular/forms/signals';
import { ToastService } from '../../../../../core/services/toast.service';

@Component({
  selector: 'app-add-home-expense-modal',
  imports: [FormField],
  templateUrl: './add-home-expense-modal.html',
})
export class AddHomeExpenseModalComponent {
  private homeService = inject(HomeService);
  private toast = inject(ToastService);

  closeModal = output<void>();

  userInHome = this.homeService.users;

  expenseModel = signal<HomeExpenseRequest>(this.emptyExpense());
  selectedUserId = signal<string | null>(null);
  submitting = signal(false);

  expenseForm = form(this.expenseModel, (path) => {
    required(path.name, { message: 'Le nom est requis' });
    required(path.payerId, { message: 'Choisissez un payeur' });
    validate(path.userConcernedId, ({ value }) =>
      value().length === 0 ? { kind: 'required', message: 'Ajoutez au moins un utilisateur concerné' } : null,
    );
    min(path.amount, 0.01, { message: 'Le montant doit être supérieur à 0' });
  });
  // Users already added to the expense, resolved to their name/email for display
  selectedUsers = computed(() => {
    const ids = this.expenseModel().userConcernedId;
    return this.userInHome().filter((u) => ids.includes(u.id));
  });
  // Candidates left in the "add user" dropdown: not already added, and not the payer
  usersToAdd = computed(() => {
    const ids = this.expenseModel().userConcernedId;
    const payerId = this.expenseModel().payerId;
    return this.userInHome().filter((u) => !ids.includes(u.id) && u.id !== payerId);
  });

  constructor() {
    effect(() => {
      const homeId = this.homeService.selectedHome()?.id;
      if (homeId) {
        this.expenseModel.update((expense) => ({ ...expense, homeId }));
      }
    });
  }

  onSelectUserChange(value: string) {
    this.selectedUserId.set(value || null);
  }

  addUserToExpense() {
    const userId = this.selectedUserId();
    if (!userId) return;
    this.expenseModel.update((expense) => ({
      ...expense,
      userConcernedId: expense.userConcernedId.includes(userId)
        ? expense.userConcernedId
        : [...expense.userConcernedId, userId],
    }));
    this.selectedUserId.set(null);
  }

  removeUserFromExpense(userId: string) {
    this.expenseModel.update((expense) => ({
      ...expense,
      userConcernedId: expense.userConcernedId.filter((id) => id !== userId),
    }));
  }

  onSubmit() {
    if (this.expenseForm().invalid()) {
      this.expenseForm().markAsTouched();
      return;
    }
    this.submitting.set(true);
    // Amount is entered in euros but stored as integer cents in the backend
    const payload = { ...this.expenseModel(), amount: Math.round(this.expenseModel().amount * 100) };
    this.homeService.addExpense(payload).subscribe({
      next: () => {
        this.submitting.set(false);
        this.toast.success('Dépense ajoutée');
        this.closeModal.emit();
      },
      error: () => {
        this.submitting.set(false);
        this.toast.error("Impossible d'ajouter cette dépense");
      },
    });
  }

  private emptyExpense(): HomeExpenseRequest {
    return {
      name: '',
      amount: 0,
      homeId: this.homeService.selectedHome()?.id ?? '',
      userConcernedId: [],
      payerId: '',
    };
  }
}
