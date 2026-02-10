export interface Subscription {
  id: number;
  userId: number;
  topicId: number;
  topicName: string;
  topicDescription: string;
  subscribedAt: string;
}

export interface SubscribeRequest {
  userId: number;
  topicId: number;
}
