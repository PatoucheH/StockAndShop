import { Component, inject, output } from '@angular/core';
import { FormBuilder, FormControl, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ShoppingListService } from '../../shopping-list.service';
import { ShoppingListRequest } from '../../shopping-list.models';
import { HomeService } from '../../../../shared/services/home.service';
import { ToastService } from '../../../../core/services/toast.service';
import { FieldErrorComponent } from '../../../../shared/components/field-error/field-error';

@Component({
  selector: 'app-add-shopping-list',
  imports: [FormsModule, ReactiveFormsModule, FieldErrorComponent],
  templateUrl: './add-shopping-list.html',
})
export class AddShoppingListComponent {
  fb = inject(FormBuilder);
  shoppingListService = inject(ShoppingListService);
  homeService = inject(HomeService);
  toast = inject(ToastService);

  closeModal = output<void>();

  get homeId(): string | undefined {
    return this.homeService.selectedHome()?.id;
  }

  form = this.fb.group({
    name: new FormControl('', [Validators.required]),
    description: new FormControl(''),
  });

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    if (!this.homeId) return;
    const request: ShoppingListRequest = this.form.getRawValue() as ShoppingListRequest;
    this.shoppingListService.createShoppingList(request, this.homeId).subscribe({
      next: () => {
        this.homeService.shoppingListsResource.reload();
        this.toast.success('Liste créée');
        this.closeModal.emit();
      },
      error: () => this.toast.error('Impossible de créer la liste'),
    });
  }
}
