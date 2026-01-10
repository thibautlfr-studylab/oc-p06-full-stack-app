import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  registerForm: FormGroup;
  errorMessage = '';
  isLoading = false;

  passwordPattern = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\S+$).{8,}$/;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.pattern(this.passwordPattern)]]
    });
  }

  goBack(): void {
    this.router.navigate(['/']);
  }

  getPasswordErrors(): string[] {
    const errors: string[] = [];
    const password = this.registerForm.get('password')?.value || '';

    if (password.length < 8) {
      errors.push('Au moins 8 caracteres');
    }
    if (!/[0-9]/.test(password)) {
      errors.push('Au moins 1 chiffre');
    }
    if (!/[a-z]/.test(password)) {
      errors.push('Au moins 1 minuscule');
    }
    if (!/[A-Z]/.test(password)) {
      errors.push('Au moins 1 majuscule');
    }
    if (!/[@#$%^&+=!]/.test(password)) {
      errors.push('Au moins 1 caractere special (@#$%^&+=!)');
    }

    return errors;
  }

  onSubmit(): void {
    if (this.registerForm.invalid) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.authService.register(this.registerForm.value).subscribe({
      next: () => {
        this.router.navigate(['/themes']);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.error || 'Une erreur est survenue';
      }
    });
  }
}
