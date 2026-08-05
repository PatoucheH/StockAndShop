import { Component, input, output, signal } from '@angular/core';
import { User } from '../../../../../shared/models/user.models';
import { ConfirmModalComponent } from '../../../../../shared/components/confirm-modal/confirm-modal';

@Component({
  selector: 'app-home-member-list',
  imports: [ConfirmModalComponent],
  templateUrl: './home-member-list.html',
})
export class HomeMemberListComponent {
  users = input.required<User[]>();
  isOwner = input.required<boolean>();
  userRemoved = output<string>();
  transferOwnership = output<string>();

  pendingUserId = signal<string | null>(null);
  pendingTransferUserId = signal<string | null>(null);

  confirmRemove(userId: string) {
    this.pendingUserId.set(userId);
  }

  onRemoveConfirmed() {
    const id = this.pendingUserId();
    if (!id) return;
    this.userRemoved.emit(id);
    this.pendingUserId.set(null);
  }

  confirmTransfer(userId: string) {
    this.pendingTransferUserId.set(userId);
  }

  onTransferConfirmed() {
    const id = this.pendingTransferUserId();
    if (!id) return;
    this.transferOwnership.emit(id);
    this.pendingTransferUserId.set(null);
  }
}
