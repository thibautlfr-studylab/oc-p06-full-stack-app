import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { User } from '../../interfaces/user.interface';
import { PASSWORD_PATTERN, getPasswordErrors } from '../../shared/validators/password.validator';

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
    const password = this.profileForm.get('password')?.value || '';
    if (!password) {
      return [];
    }
    return getPasswordErrors(password);
  }

  isPasswordValid(): boolean {
    const password = this.profileForm.get('password')?.value;
    if (!password) {
      return true;
    }
    return PASSWORD_PATTERN.test(password);
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
