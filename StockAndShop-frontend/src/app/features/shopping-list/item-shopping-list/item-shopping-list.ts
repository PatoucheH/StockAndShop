import { Component, input, InputSignal } from '@angular/core';
import { ProductItem } from '../../../shared/models/productItem.models';
import { ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-item-shopping-list',
  imports: [ReactiveFormsModule],
  templateUrl: './item-shopping-list.html',
  styleUrl: './item-shopping-list.scss',
})
export class ItemShoppingList {
  item = input.required<ProductItem>();
}
