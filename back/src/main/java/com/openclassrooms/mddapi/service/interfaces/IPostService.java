package com.openclassrooms.mddapi.service.interfaces;

import com.openclassrooms.mddapi.dto.PostDTO;
import com.openclassrooms.mddapi.dto.request.CreatePostRequest;

import java.util.List;

/**
 * Service interface for Post operations.
 */
public interface IPostService {

  /**
   * Get feed posts for a user based on subscribed topics.
   *
   * @param userId    the user ID
   * @param ascending if true, sort oldest first; if false, newest first
   * @return list of post DTOs from subscribed topics
   */
  List<PostDTO> getFeedForUser(Long userId, boolean ascending);

  /**
   * Get a post by its ID.
   *
   * @param id the post ID
   * @return the post DTO
   */
  PostDTO getPostById(Long id);

  /**
   * Create a new post.
   *
   * @param authorId the author's user ID
   * @param request  the creation request containing topicId, title, and content
   * @return the created post DTO
   */
  PostDTO createPost(Long authorId, CreatePostRequest request);
}
