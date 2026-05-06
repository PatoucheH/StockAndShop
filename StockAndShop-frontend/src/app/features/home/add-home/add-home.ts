import { Component, inject, output } from '@angular/core';
import { HomeService } from '../home.service';
import { HomeRequest } from '../home.model';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-add-home',
  imports: [ReactiveFormsModule],
  templateUrl: './add-home.html',
  styleUrl: './add-home.scss',
})
export class AddHomeComponent {

  fb = inject(FormBuilder);
  homeService = inject(HomeService);

  closeModal = output<void>();
  form = this.fb.group({
    name: new FormControl('', [Validators.required]),
    description: new FormControl('', [Validators.required]),
  });

  submit() {
    const request: HomeRequest = this.form.getRawValue() as HomeRequest;
    this.homeService.createNewHome(request);
    this.closeModal.emit();
  }

}
