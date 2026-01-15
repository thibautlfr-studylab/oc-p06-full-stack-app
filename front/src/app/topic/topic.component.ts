import { Component, OnInit } from '@angular/core';
import { Topic } from '../interfaces/topic.interface';
import { TopicService } from '../services/topic.service';
import { SubscriptionService } from '../services/subscription.service';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-topic',
  templateUrl: './topic.component.html',
  styleUrls: ['./topic.component.css'],
  standalone: false
})
export class TopicComponent implements OnInit {
  topics: Topic[] = [];
  loading = true;
  error: string | null = null;
  subscribedTopicIds: Set<number> = new Set();
  subscribingTopicId: number | null = null;

  constructor(
    private topicService: TopicService,
    private subscriptionService: SubscriptionService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.loadTopics();
    this.loadSubscribedTopicIds();
  }

  private loadTopics(): void {
    this.topicService.getTopics().subscribe({
      next: (data) => {
        this.topics = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des thèmes';
        this.loading = false;
        console.error(err);
      }
    });
  }

  private loadSubscribedTopicIds(): void {
    const user = this.authService.getCurrentUser();
    if (user) {
      this.subscriptionService.getSubscribedTopicIds(user.id).subscribe({
        next: (ids) => {
          this.subscribedTopicIds = new Set(ids);
        },
        error: (err) => {
          console.error('Erreur lors du chargement des abonnements', err);
        }
      });
    }
  }

  isSubscribed(topicId: number): boolean {
    return this.subscribedTopicIds.has(topicId);
  }

  subscribe(topicId: number): void {
    const user = this.authService.getCurrentUser();
    if (!user || this.subscribingTopicId !== null) {
      return;
    }

    this.subscribingTopicId = topicId;
    this.subscriptionService.subscribe({ userId: user.id, topicId }).subscribe({
      next: () => {
        this.subscribedTopicIds.add(topicId);
        this.subscribingTopicId = null;
      },
      error: (err) => {
        console.error('Erreur lors de l\'abonnement', err);
        this.subscribingTopicId = null;
      }
    });
  }
}
