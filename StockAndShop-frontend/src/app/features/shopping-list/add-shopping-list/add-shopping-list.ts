import { Component, inject, output } from '@angular/core';
import {
  FormBuilder,
  FormControl,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';

import { ShoppingListService } from '../shopping-list.service';
import { ShoppingListRequest } from '../shopping-list.models';
import { HomeService } from '../../home/home.service';

@Component({
  selector: 'app-add-shopping-list',
  imports: [FormsModule, ReactiveFormsModule],
  templateUrl: './add-shopping-list.html',
  styleUrl: './add-shopping-list.scss',
})
export class AddShoppingListComponent {
  fb = inject(FormBuilder);
  shoppingListService = inject(ShoppingListService);
  homeService = inject(HomeService);

  closeModal = output<void>();

  get homeId(): string | undefined {
    return this.homeService.selectedHome()?.id;
  }

  form = this.fb.group({
    name: new FormControl('', [Validators.required]),
    description: new FormControl('', [Validators.required]),
  });

  submit() {
    if (!this.homeId) return;
    const request: ShoppingListRequest = this.form.getRawValue() as ShoppingListRequest;
    this.shoppingListService.createShoppingList(request, this.homeId).subscribe(() => {
      this.homeService.reloadShoppingList();
    });
    this.closeModal.emit();
  }
}
