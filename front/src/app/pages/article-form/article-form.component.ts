import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Topic } from '../../interfaces/topic.interface';
import { TopicService } from '../../services/topic.service';
import { SubscriptionService } from '../../services/subscription.service';
import { PostService } from '../../services/post.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-article-form',
  templateUrl: './article-form.component.html',
  styleUrls: ['./article-form.component.css'],
  standalone: false
})
export class ArticleFormComponent implements OnInit {
  articleForm: FormGroup;
  subscribedTopics: Topic[] = [];
  loading = true;
  isLoading = false;
  errorMessage = '';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private topicService: TopicService,
    private subscriptionService: SubscriptionService,
    private postService: PostService,
    private authService: AuthService
  ) {
    this.articleForm = this.fb.group({
      topicId: ['', [Validators.required]],
      title: ['', [Validators.required, Validators.maxLength(255)]],
      content: ['', [Validators.required]]
    });
  }

  ngOnInit(): void {
    this.loadSubscribedTopics();
  }

  private loadSubscribedTopics(): void {
    const user = this.authService.getCurrentUser();
    if (!user) {
      return;
    }

    forkJoin({
      topics: this.topicService.getTopics(),
      subscribedIds: this.subscriptionService.getSubscribedTopicIds(user.id)
    }).subscribe({
      next: ({ topics, subscribedIds }) => {
        const subscribedSet = new Set(subscribedIds);
        this.subscribedTopics = topics.filter(t => subscribedSet.has(t.id));
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des themes', err);
        this.loading = false;
      }
    });
  }

  goBack(): void {
    this.router.navigate(['/articles']);
  }

  onSubmit(): void {
    if (this.articleForm.invalid || this.isLoading) {
      return;
    }

    const user = this.authService.getCurrentUser();
    if (!user) {
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const request = {
      topicId: this.articleForm.get('topicId')?.value,
      title: this.articleForm.get('title')?.value,
      content: this.articleForm.get('content')?.value
    };

    this.postService.createPost(user.id, request).subscribe({
      next: () => {
        this.router.navigate(['/articles']);
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || 'Une erreur est survenue';
      }
    });
  }
}
