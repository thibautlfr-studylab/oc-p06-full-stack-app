import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Post } from '../../interfaces/post.interface';
import { Comment } from '../../interfaces/comment.interface';
import { PostService } from '../../services/post.service';
import { CommentService } from '../../services/comment.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-article-detail',
  templateUrl: './article-detail.component.html',
  styleUrls: ['./article-detail.component.css'],
  standalone: false
})
export class ArticleDetailComponent implements OnInit {
  post: Post | null = null;
  comments: Comment[] = [];
  loading = true;
  loadingComments = true;
  error: string | null = null;
  newComment = '';
  submitting = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private postService: PostService,
    private commentService: CommentService,
    private authService: AuthService
  ) {
  }

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadPost(+id);
      this.loadComments(+id);
    }
  }

  goBack(): void {
    this.router.navigate(['/articles']);
  }

  submitComment(): void {
    if (!this.newComment.trim() || !this.post || this.submitting) {
      return;
    }

    const user = this.authService.getCurrentUser();
    if (!user) {
      return;
    }

    this.submitting = true;
    this.commentService.addComment(this.post.id, user.id, {content: this.newComment}).subscribe({
      next: (comment) => {
        this.comments.push(comment);
        this.newComment = '';
        this.submitting = false;
      },
      error: (err) => {
        console.error('Erreur lors de l\'ajout du commentaire', err);
        this.submitting = false;
      }
    });
  }

  private loadPost(id: number): void {
    this.postService.getPostById(id).subscribe({
      next: (data) => {
        this.post = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement de l\'article';
        this.loading = false;
        console.error(err);
      }
    });
  }

  private loadComments(postId: number): void {
    this.commentService.getCommentsForPost(postId).subscribe({
      next: (data) => {
        this.comments = data;
        this.loadingComments = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des commentaires', err);
        this.loadingComments = false;
      }
    });
  }
}
