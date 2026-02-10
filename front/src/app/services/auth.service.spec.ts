import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { AuthResponse, LoginRequest, RegisterRequest, User } from '../interfaces/user.interface';
import { environment } from '../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  const mockAuthResponse: AuthResponse = {
    id: 1,
    username: 'testuser',
    email: 'test@example.com',
    token: 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6InRlc3R1c2VyIiwiZXhwIjo5OTk5OTk5OTk5fQ.test',
    message: 'Success'
  };

  beforeEach(() => {
    // Clear localStorage before each test
    localStorage.clear();

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [AuthService]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('register', () => {

    it('should send POST request to register endpoint', () => {
      const registerRequest: RegisterRequest = {
        username: 'testuser',
        email: 'test@example.com',
        password: 'Password1!'
      };

      service.register(registerRequest).subscribe(response => {
        expect(response).toEqual(mockAuthResponse);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/api/auth/register`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(registerRequest);
      req.flush(mockAuthResponse);
    });

    it('should store token after successful registration', () => {
      const registerRequest: RegisterRequest = {
        username: 'testuser',
        email: 'test@example.com',
        password: 'Password1!'
      };

      service.register(registerRequest).subscribe();

      const req = httpMock.expectOne(`${environment.apiUrl}/api/auth/register`);
      req.flush(mockAuthResponse);

      expect(localStorage.getItem('mdd_token')).toBe(mockAuthResponse.token);
    });

  });

  describe('login', () => {

    it('should send POST request to login endpoint', () => {
      const loginRequest: LoginRequest = {
        identifier: 'test@example.com',
        password: 'Password1!'
      };

      service.login(loginRequest).subscribe(response => {
        expect(response).toEqual(mockAuthResponse);
      });

      const req = httpMock.expectOne(`${environment.apiUrl}/api/auth/login`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(loginRequest);
      req.flush(mockAuthResponse);
    });

    it('should store token after successful login', () => {
      const loginRequest: LoginRequest = {
        identifier: 'test@example.com',
        password: 'Password1!'
      };

      service.login(loginRequest).subscribe();

      const req = httpMock.expectOne(`${environment.apiUrl}/api/auth/login`);
      req.flush(mockAuthResponse);

      expect(localStorage.getItem('mdd_token')).toBe(mockAuthResponse.token);
    });

  });

  describe('logout', () => {

    it('should remove token from localStorage', () => {
      localStorage.setItem('mdd_token', 'some-token');

      service.logout();

      expect(localStorage.getItem('mdd_token')).toBeNull();
    });

    it('should set current user to null', () => {
      service.logout();

      expect(service.getCurrentUser()).toBeNull();
    });

  });

  describe('getToken', () => {

    it('should return token from localStorage', () => {
      const token = 'test-token';
      localStorage.setItem('mdd_token', token);

      expect(service.getToken()).toBe(token);
    });

    it('should return null when no token exists', () => {
      expect(service.getToken()).toBeNull();
    });

  });

  describe('isLoggedIn', () => {

    it('should return false when no user is logged in', () => {
      expect(service.isLoggedIn()).toBeFalse();
    });

    it('should return true when user is logged in', () => {
      const loginRequest: LoginRequest = {
        identifier: 'test@example.com',
        password: 'Password1!'
      };

      service.login(loginRequest).subscribe();

      const req = httpMock.expectOne(`${environment.apiUrl}/api/auth/login`);
      req.flush(mockAuthResponse);

      expect(service.isLoggedIn()).toBeTrue();
    });

  });

  describe('getCurrentUser', () => {

    it('should return null when no user is logged in', () => {
      expect(service.getCurrentUser()).toBeNull();
    });

  });

  describe('updateCurrentUser', () => {

    it('should update current user subject', () => {
      const user: User = {
        id: 1,
        username: 'newuser',
        email: 'new@example.com'
      };

      service.updateCurrentUser(user);

      expect(service.getCurrentUser()).toEqual(user);
    });

    it('should emit new user through currentUser$ observable', (done) => {
      const user: User = {
        id: 1,
        username: 'newuser',
        email: 'new@example.com'
      };

      service.currentUser$.subscribe(currentUser => {
        if (currentUser) {
          expect(currentUser).toEqual(user);
          done();
        }
      });

      service.updateCurrentUser(user);
    });

  });

});
