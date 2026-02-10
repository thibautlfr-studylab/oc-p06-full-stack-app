import { Component, EventEmitter, Input, Output } from '@angular/core';
import { TopicCardData, TopicCardMode } from './topic-card.interface';

@Component({
  selector: 'app-topic-card',
  templateUrl: './topic-card.component.html',
  styleUrls: ['./topic-card.component.css'],
  standalone: false
})
export class TopicCardComponent {
  @Input() topic!: TopicCardData;
  @Input() mode: TopicCardMode = 'subscribe';
  @Input() isSubscribed = false;
  @Input() isLoading = false;

  @Output() action = new EventEmitter<number>();

  onAction(): void {
    if (!this.isLoading) {
      this.action.emit(this.topic.id);
    }
  }

  getButtonText(): string {
    if (this.mode === 'subscribe') {
      if (this.isLoading) {
        return 'Abonnement...';
      }
      return this.isSubscribed ? 'Déjà abonné' : "S'abonner";
    } else {
      return this.isLoading ? 'Désabonnement...' : 'Se désabonner';
    }
  }

  isButtonDisabled(): boolean {
    return this.isLoading || (this.mode === 'subscribe' && this.isSubscribed);
  }

  getButtonClass(): string {
    if (this.mode === 'subscribe' && this.isSubscribed) {
      return 'subscribed-button';
    }
    return 'action-button';
  }
}
