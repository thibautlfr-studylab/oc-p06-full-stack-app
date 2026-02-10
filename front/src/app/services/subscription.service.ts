import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { SubscribeRequest, Subscription } from '../interfaces/subscription.interface';

@Injectable({
  providedIn: 'root'
})
export class SubscriptionService {

  private apiUrl = `${environment.apiUrl}/api/subscriptions`;

  constructor(private http: HttpClient) {
  }

  getUserSubscriptions(userId: number): Observable<Subscription[]> {
    return this.http.get<Subscription[]>(`${this.apiUrl}/user/${userId}`);
  }

  getSubscribedTopicIds(userId: number): Observable<number[]> {
    return this.http.get<number[]>(`${this.apiUrl}/user/${userId}/topic-ids`);
  }

  subscribe(request: SubscribeRequest): Observable<Subscription> {
    return this.http.post<Subscription>(this.apiUrl, request);
  }

  unsubscribe(userId: number, topicId: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/user/${userId}/topic/${topicId}`);
  }
}
