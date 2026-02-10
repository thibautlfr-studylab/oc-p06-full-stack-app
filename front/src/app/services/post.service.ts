import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { CreatePostRequest, Post } from '../interfaces/post.interface';

@Injectable({
  providedIn: 'root'
})
export class PostService {

  private apiUrl = `${environment.apiUrl}/api/posts`;

  constructor(private http: HttpClient) {
  }

  getFeed(userId: number, ascending: boolean = false): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.apiUrl}/feed/${userId}?ascending=${ascending}`);
  }

  getPostById(id: number): Observable<Post> {
    return this.http.get<Post>(`${this.apiUrl}/${id}`);
  }

  createPost(userId: number, request: CreatePostRequest): Observable<Post> {
    return this.http.post<Post>(`${this.apiUrl}/user/${userId}`, request);
  }
}
