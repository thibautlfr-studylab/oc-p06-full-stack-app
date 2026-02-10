package com.openclassrooms.mddapi.service.interfaces;

import com.openclassrooms.mddapi.dto.CommentDTO;
import com.openclassrooms.mddapi.dto.request.CreateCommentRequest;

import java.util.List;

/**
 * Service interface for Comment operations.
 */
public interface ICommentService {

  /**
   * Get all comments for a specific post.
   *
   * @param postId the post ID
   * @return list of comment DTOs ordered by creation date ascending
   */
  List<CommentDTO> getCommentsForPost(Long postId);

  /**
   * Add a comment to a post.
   *
   * @param postId   the post ID
   * @param authorId the author's user ID
   * @param request  the creation request containing content
   * @return the created comment DTO
   */
  CommentDTO addComment(Long postId, Long authorId, CreateCommentRequest request);
}
