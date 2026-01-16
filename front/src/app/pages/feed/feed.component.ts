import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Post } from '../../interfaces/post.interface';
import { PostService } from '../../services/post.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.css'],
  standalone: false
})
export class FeedComponent implements OnInit {
  posts: Post[] = [];
  loading = true;
  error: string | null = null;
  ascending = false;

  constructor(
    private postService: PostService,
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadFeed();
  }

  private loadFeed(): void {
    const user = this.authService.getCurrentUser();
    if (!user) {
      return;
    }

    this.loading = true;
    this.postService.getFeed(user.id, this.ascending).subscribe({
      next: (data) => {
        this.posts = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des articles';
        this.loading = false;
        console.error(err);
      }
    });
  }

  toggleSort(): void {
    this.ascending = !this.ascending;
    this.loadFeed();
  }

  navigateToPost(postId: number): void {
    this.router.navigate(['/articles', postId]);
  }

  navigateToCreate(): void {
    this.router.navigate(['/articles/create']);
  }
}
