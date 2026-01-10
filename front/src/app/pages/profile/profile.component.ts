import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { User } from '../../interfaces/user.interface';

@Component({
  selector: 'app-profile',
  standalone: false,
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {
  profileForm: FormGroup;
  currentUser: User | null = null;
  successMessage = '';
  errorMessage = '';
  isLoading = false;

  passwordPattern = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\S+$).{8,}$/;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private userService: UserService
  ) {
    this.profileForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['']
    });
  }

  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    if (this.currentUser) {
      this.profileForm.patchValue({
        username: this.currentUser.username,
        email: this.currentUser.email
      });
    }
  }

  getPasswordErrors(): string[] {
    const errors: string[] = [];
    const password = this.profileForm.get('password')?.value || '';

    if (!password) {
      return [];
    }

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

  isPasswordValid(): boolean {
    const password = this.profileForm.get('password')?.value;
    if (!password) {
      return true;
    }
    return this.passwordPattern.test(password);
  }

  onSubmit(): void {
    if (this.profileForm.invalid || !this.isPasswordValid()) {
      return;
    }

    if (!this.currentUser) {
      return;
    }

    this.isLoading = true;
    this.successMessage = '';
    this.errorMessage = '';

    const request: any = {
      username: this.profileForm.get('username')?.value,
      email: this.profileForm.get('email')?.value
    };

    const password = this.profileForm.get('password')?.value;
    if (password) {
      request.password = password;
    }

    this.userService.updateProfile(this.currentUser.id, request).subscribe({
      next: (updatedUser) => {
        this.isLoading = false;
        this.successMessage = 'Profil mis a jour avec succes';
        this.authService.updateCurrentUser(updatedUser);
        this.profileForm.patchValue({ password: '' });
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.error || 'Une erreur est survenue';
      }
    });
  }
}
