import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { SubscriptionService } from '../../services/subscription.service';
import { User } from '../../interfaces/user.interface';
import { Subscription } from '../../interfaces/subscription.interface';
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
  subscriptions: Subscription[] = [];
  loadingSubscriptions = true;
  unsubscribingTopicId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private userService: UserService,
    private subscriptionService: SubscriptionService
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
      this.loadSubscriptions();
    }
  }

  private loadSubscriptions(): void {
    if (!this.currentUser) {
      return;
    }
    this.loadingSubscriptions = true;
    this.subscriptionService.getUserSubscriptions(this.currentUser.id).subscribe({
      next: (subscriptions) => {
        this.subscriptions = subscriptions;
        this.loadingSubscriptions = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des abonnements', err);
        this.loadingSubscriptions = false;
      }
    });
  }

  unsubscribe(topicId: number): void {
    if (!this.currentUser || this.unsubscribingTopicId !== null) {
      return;
    }

    this.unsubscribingTopicId = topicId;
    this.subscriptionService.unsubscribe(this.currentUser.id, topicId).subscribe({
      next: () => {
        this.subscriptions = this.subscriptions.filter(sub => sub.topicId !== topicId);
        this.unsubscribingTopicId = null;
      },
      error: (err) => {
        console.error('Erreur lors du desabonnement', err);
        this.unsubscribingTopicId = null;
      }
    });
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
        this.successMessage = 'Profil mis à jour avec succès';
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
