import { Component, inject, signal } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router, RouterLink } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { LoginRequest } from '../../../../core/models/auth.models';
import { FieldErrorComponent } from '../../../../shared/components/field-error/field-error';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink, FieldErrorComponent],
  templateUrl: './login.html',

})
export class LoginComponent {

  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  loading = signal(false);
  error = signal<string | null>(null);
  showPassword = signal(false);

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.loading.set(true);
    this.error.set(null);
    const request: LoginRequest = this.form.getRawValue() as LoginRequest;
    this.authService.login(request).subscribe({
      next: (response) => {
        this.authService.saveToken(response.token, response.email, response.displayName);
        this.router.navigate(['/']);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Email ou mot de passe incorrect');
        this.loading.set(false);
      },
    });
  }
}
