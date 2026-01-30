import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { of, throwError } from 'rxjs';
import { TopicComponent } from './topic.component';
import { TopicService } from '../services/topic.service';
import { SubscriptionService } from '../services/subscription.service';
import { AuthService } from '../services/auth.service';
import { Topic } from '../interfaces/topic.interface';
import { User } from '../interfaces/user.interface';
import { Subscription } from '../interfaces/subscription.interface';

describe('TopicComponent', () => {
  let component: TopicComponent;
  let fixture: ComponentFixture<TopicComponent>;
  let topicServiceSpy: jasmine.SpyObj<TopicService>;
  let subscriptionServiceSpy: jasmine.SpyObj<SubscriptionService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  const mockTopics: Topic[] = [
    { id: 1, name: 'Java', description: 'Programming language' },
    { id: 2, name: 'Angular', description: 'Frontend framework' },
    { id: 3, name: 'Spring', description: 'Backend framework' }
  ];

  const mockUser: User = {
    id: 1,
    username: 'testuser',
    email: 'test@example.com'
  };

  beforeEach(async () => {
    topicServiceSpy = jasmine.createSpyObj('TopicService', ['getTopics']);
    subscriptionServiceSpy = jasmine.createSpyObj('SubscriptionService', [
      'getSubscribedTopicIds',
      'subscribe'
    ]);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['getCurrentUser']);

    // Default mock returns
    topicServiceSpy.getTopics.and.returnValue(of(mockTopics));
    subscriptionServiceSpy.getSubscribedTopicIds.and.returnValue(of([1]));
    authServiceSpy.getCurrentUser.and.returnValue(mockUser);

    await TestBed.configureTestingModule({
      declarations: [TopicComponent],
      providers: [
        { provide: TopicService, useValue: topicServiceSpy },
        { provide: SubscriptionService, useValue: subscriptionServiceSpy },
        { provide: AuthService, useValue: authServiceSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(TopicComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load topics on init', fakeAsync(() => {
    fixture.detectChanges();
    tick();

    expect(topicServiceSpy.getTopics).toHaveBeenCalled();
    expect(component.topics).toEqual(mockTopics);
    expect(component.loading).toBeFalse();
  }));

  it('should load subscribed topic ids on init when user is logged in', fakeAsync(() => {
    fixture.detectChanges();
    tick();

    expect(subscriptionServiceSpy.getSubscribedTopicIds).toHaveBeenCalledWith(mockUser.id);
    expect(component.subscribedTopicIds.has(1)).toBeTrue();
  }));

  it('should not load subscribed topic ids when user is not logged in', fakeAsync(() => {
    authServiceSpy.getCurrentUser.and.returnValue(null);
    fixture.detectChanges();
    tick();

    expect(subscriptionServiceSpy.getSubscribedTopicIds).not.toHaveBeenCalled();
  }));

  it('should set error when topics loading fails', fakeAsync(() => {
    topicServiceSpy.getTopics.and.returnValue(throwError(() => new Error('Network error')));
    fixture.detectChanges();
    tick();

    expect(component.error).toBe('Erreur lors du chargement des thèmes');
    expect(component.loading).toBeFalse();
  }));

  describe('isSubscribed', () => {

    it('should return true when topic is in subscribed set', fakeAsync(() => {
      fixture.detectChanges();
      tick();

      expect(component.isSubscribed(1)).toBeTrue();
    }));

    it('should return false when topic is not in subscribed set', fakeAsync(() => {
      fixture.detectChanges();
      tick();

      expect(component.isSubscribed(999)).toBeFalse();
    }));

  });

  describe('subscribe', () => {

    it('should not subscribe when user is not logged in', fakeAsync(() => {
      authServiceSpy.getCurrentUser.and.returnValue(null);
      fixture.detectChanges();
      tick();

      component.subscribe(2);

      expect(subscriptionServiceSpy.subscribe).not.toHaveBeenCalled();
    }));

    it('should not subscribe when already subscribing to another topic', fakeAsync(() => {
      fixture.detectChanges();
      tick();

      component.subscribingTopicId = 3;
      component.subscribe(2);

      expect(subscriptionServiceSpy.subscribe).not.toHaveBeenCalled();
    }));

    it('should subscribe to topic and add to subscribed set', fakeAsync(() => {
      const mockSubscription: Subscription = {
        id: 1,
        userId: mockUser.id,
        topicId: 2,
        topicName: 'Angular',
        topicDescription: 'Frontend framework',
        subscribedAt: '2024-01-01T00:00:00Z'
      };
      subscriptionServiceSpy.subscribe.and.returnValue(of(mockSubscription));
      fixture.detectChanges();
      tick();

      component.subscribe(2);
      tick();

      expect(subscriptionServiceSpy.subscribe).toHaveBeenCalledWith({
        userId: mockUser.id,
        topicId: 2
      });
      expect(component.subscribedTopicIds.has(2)).toBeTrue();
      expect(component.subscribingTopicId).toBeNull();
    }));

    it('should reset subscribingTopicId on subscription error', fakeAsync(() => {
      subscriptionServiceSpy.subscribe.and.returnValue(throwError(() => new Error('Error')));
      fixture.detectChanges();
      tick();

      component.subscribe(2);
      tick();

      expect(component.subscribingTopicId).toBeNull();
      expect(component.subscribedTopicIds.has(2)).toBeFalse();
    }));

  });

});
