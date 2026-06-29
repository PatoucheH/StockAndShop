import { Component, inject, signal } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { Router, RouterLink } from '@angular/router';
import { RegisterRequest } from '../../../../core/models/auth.models';
import { FieldErrorComponent } from '../../../../shared/components/field-error/field-error';

function passwordMatchValidator(): ValidatorFn {
  return (group: AbstractControl): ValidationErrors | null => {
    const password = group.get('password')?.value;
    const confirm = group.get('confirmPassword')?.value;
    return password === confirm ? null : { passwordMismatch: true };
  };
}

@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink, FieldErrorComponent],
  templateUrl: './register.html',

})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  form = this.fb.group(
    {
      email: ['', [Validators.required, Validators.email]],
      username: ['', Validators.required],
      password: ['', Validators.required],
      confirmPassword: ['', Validators.required],
    },
    { validators: passwordMatchValidator() }
  );

  loading = signal(false);
  error = signal<string | null>(null);
  showPassword = signal(false);
  showConfirmPassword = signal(false);

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    this.error.set(null);
    const { confirmPassword: _confirm, ...requestData } = this.form.getRawValue();
    const request = requestData as RegisterRequest;

    this.authService.register(request).subscribe({
      next: (response) => {
        this.authService.saveToken(response.token, response.email);
        this.router.navigate(['/']);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Email, username ou password incorrect');
        this.loading.set(false);
      },
    });
  }
}
