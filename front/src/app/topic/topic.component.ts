import { Component, OnInit } from '@angular/core';
import { Topic } from '../interfaces/topic.interface';
import { TopicService } from '../services/topic.service';

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

  constructor(private topicService: TopicService) { }

  ngOnInit(): void {
    this.topicService.getTopics().subscribe({
      next: (data) => {
        this.topics = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Erreur lors du chargement des topics';
        this.loading = false;
        console.error(err);
      }
    });
  }
}
