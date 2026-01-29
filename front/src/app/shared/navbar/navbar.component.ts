import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { User } from '../../interfaces/user.interface';
import { ResponsiveService } from '../../services/responsive.service';

@Component({
  selector: 'app-navbar',
  standalone: false,
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']
})
export class NavbarComponent implements OnInit, OnDestroy {
  isLoggedIn = false;
  currentUser: User | null = null;
  isMobileMenuOpen = false;
  isMobile = false;
  private subscription: Subscription | null = null;
  private responsiveSubscription: Subscription | null = null;

  constructor(
    private authService: AuthService,
    private router: Router,
    private responsiveService: ResponsiveService
  ) {
  }

  ngOnInit(): void {
    this.subscription = this.authService.currentUser$.subscribe(user => {
      this.isLoggedIn = user !== null;
      this.currentUser = user;
    });

    this.responsiveSubscription = this.responsiveService.isMobile$.subscribe(isMobile => {
      this.isMobile = isMobile;
      if (!isMobile) {
        this.closeMobileMenu();
      }
    });
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
    if (this.responsiveSubscription) {
      this.responsiveSubscription.unsubscribe();
    }
  }

  toggleMobileMenu(): void {
    this.isMobileMenuOpen = !this.isMobileMenuOpen;
  }

  closeMobileMenu(): void {
    this.isMobileMenuOpen = false;
  }

  logout(): void {
    this.authService.logout();
    this.closeMobileMenu();
    this.router.navigate(['/']);
  }

  navigateTo(path: string): void {
    this.closeMobileMenu();
    this.router.navigate([path]);
  }
}
