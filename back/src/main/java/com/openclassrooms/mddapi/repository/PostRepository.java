package com.openclassrooms.mddapi.repository;

import com.openclassrooms.mddapi.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Post entity operations.
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

  /**
   * Find posts by topic IDs, ordered by creation date descending (newest first).
   *
   * @param topicIds list of topic IDs to filter by
   * @return list of posts matching the topic IDs
   */
  List<Post> findByTopicIdInOrderByCreatedAtDesc(List<Long> topicIds);

  /**
   * Find posts by topic IDs, ordered by creation date ascending (oldest first).
   *
   * @param topicIds list of topic IDs to filter by
   * @return list of posts matching the topic IDs
   */
  List<Post> findByTopicIdInOrderByCreatedAtAsc(List<Long> topicIds);

  /**
   * Find a post by ID with author and topic eagerly loaded.
   *
   * @param id the post ID
   * @return optional containing the post if found
   */
  @Query("SELECT p FROM Post p JOIN FETCH p.author JOIN FETCH p.topic WHERE p.id = :id")
  Optional<Post> findByIdWithDetails(@Param("id") Long id);
}
