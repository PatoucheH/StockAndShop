import { Component, inject, output } from '@angular/core';
import { HomeService } from '../../../shared/services/home.service';
import { HomeRequest } from '../home.models';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-add-home',
  imports: [ReactiveFormsModule],
  templateUrl: './add-home.html',

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
