import { Component, inject, output } from '@angular/core';
import { HomeService } from '../../../../shared/services/home.service';
import { HomeRequest } from '../../models/home.models';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { FieldErrorComponent } from '../../../../shared/components/field-error/field-error';

@Component({
  selector: 'app-add-home',
  imports: [ReactiveFormsModule, FieldErrorComponent],
  templateUrl: './add-home.html',

})
export class AddHomeComponent {

  fb = inject(FormBuilder);
  homeService = inject(HomeService);

  closeModal = output<void>();
  form = this.fb.group({
    name: new FormControl('', [Validators.required]),
    description: new FormControl(''),
  });

  submit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const request: HomeRequest = this.form.getRawValue() as HomeRequest;
    this.homeService.createNewHome(request);
    this.closeModal.emit();
  }

}
