package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Comment entity operations.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

  /**
   * Find all comments for a post with author eagerly loaded, ordered by creation date ascending.
   *
   * @param postId the post ID
   * @return list of comments for the post
   */
  @Query("SELECT c FROM Comment c JOIN FETCH c.author WHERE c.post.id = :postId ORDER BY c.createdAt ASC")
  List<Comment> findByPostIdWithAuthor(@Param("postId") Long postId);
}
