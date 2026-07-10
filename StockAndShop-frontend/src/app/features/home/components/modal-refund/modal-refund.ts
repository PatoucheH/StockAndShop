import { Component, computed, inject, output, signal } from '@angular/core';
import { HomeService } from '../../../../shared/services/home.service';
import { FormField, form, required, min, validate } from '@angular/forms/signals';
import { ToastService } from '../../../../core/services/toast.service';

interface RefundModel {
  payerId: string;
  receiverId: string;
  amount: number;
}

@Component({
  selector: 'app-modal-refund',
  imports: [FormField],
  templateUrl: './modal-refund.html',
})
export class ModalRefundComponent {
  private homeService = inject(HomeService);
  private toast = inject(ToastService);

  closeModal = output<void>();

  userInHome = this.homeService.users;

  refundModel = signal<RefundModel>({ payerId: '', receiverId: '', amount: 0 });
  submitting = signal(false);

  refundForm = form(this.refundModel, (path) => {
    required(path.payerId, { message: 'Choisissez un payeur' });
    required(path.receiverId, { message: 'Choisissez un receveur' });
    min(path.amount, 0.01, { message: 'Le montant doit être supérieur à 0' });
    validate(path.receiverId, ({ value, valueOf }) =>
      value() && value() === valueOf(path.payerId)
        ? { kind: 'sameUser', message: 'Le payeur et le receveur doivent être différents' }
        : null,
    );
  });

  // Receiver candidates exclude whoever is currently selected as payer
  receiversToChooseFrom = computed(() => {
    const payerId = this.refundModel().payerId;
    return this.userInHome().filter((u) => u.id !== payerId);
  });

  onSubmit() {
    if (this.refundForm().invalid()) {
      this.refundForm().markAsTouched();
      return;
    }
    this.submitting.set(true);
    const { payerId, receiverId, amount } = this.refundModel();
    // Amount is entered in euros but stored as integer cents in the backend
    this.homeService.refundUser(Math.round(amount * 100), payerId, receiverId).subscribe({
      next: () => {
        this.submitting.set(false);
        this.toast.success('Remboursement enregistré');
        this.closeModal.emit();
      },
      error: () => {
        this.submitting.set(false);
        this.toast.error("Impossible d'enregistrer ce remboursement");
      },
    });
  }
}
