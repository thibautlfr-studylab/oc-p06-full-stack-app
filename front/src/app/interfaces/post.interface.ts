export interface Post {
  id: number;
  title: string;
  content: string;
  authorId: number;
  authorUsername: string;
  topicId: number;
  topicName: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreatePostRequest {
  topicId: number;
  title: string;
  content: string;
}
