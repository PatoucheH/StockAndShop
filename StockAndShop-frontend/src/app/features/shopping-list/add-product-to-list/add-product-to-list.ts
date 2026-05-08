import { Component } from '@angular/core';
import {ReactiveFormsModule} from "@angular/forms";
import { form } from '@angular/forms/signals';

@Component({
  selector: 'app-add-product-to-list',
  imports: [ReactiveFormsModule],
  templateUrl: './add-product-to-list.html',
  styleUrl: './add-product-to-list.scss',
})
export class AddProductToList {
  protected readonly form = form;
}
