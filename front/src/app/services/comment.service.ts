import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Comment, CreateCommentRequest } from '../interfaces/comment.interface';

@Injectable({
  providedIn: 'root'
})
export class CommentService {

  private apiUrl = `${environment.apiUrl}/api/comments`;

  constructor(private http: HttpClient) { }

  getCommentsForPost(postId: number): Observable<Comment[]> {
    return this.http.get<Comment[]>(`${this.apiUrl}/post/${postId}`);
  }

  addComment(postId: number, userId: number, request: CreateCommentRequest): Observable<Comment> {
    return this.http.post<Comment>(`${this.apiUrl}/post/${postId}/user/${userId}`, request);
  }
}
