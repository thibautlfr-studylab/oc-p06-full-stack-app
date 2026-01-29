import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

/**
 * HTTP interceptor that automatically adds JWT token to requests.
 * Injects the Authorization header with Bearer token for authenticated requests.
 */
@Injectable()
export class JwtInterceptor implements HttpInterceptor {

  constructor(private authService: AuthService) {}

  /**
   * Intercepts HTTP requests and adds the JWT token if available.
   *
   * @param request the outgoing HTTP request
   * @param next the next handler in the chain
   * @returns the observable of the HTTP event
   */
  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = this.authService.getToken();

    if (token) {
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    return next.handle(request);
  }
}
