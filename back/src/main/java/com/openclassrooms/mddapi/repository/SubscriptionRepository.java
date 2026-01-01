package com.openclassrooms.mddapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.openclassrooms.mddapi.model.Subscription;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;

/**
 * Repository for the Subscription entity.
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

	/**
	 * Finds all subscriptions for a user.
	 */
	List<Subscription> findByUser(User user);

	/**
	 * Finds all subscriptions for a user by their ID.
	 */
	List<Subscription> findByUserId(Long userId);

	/**
	 * Finds a specific subscription (user + topic).
	 */
	Optional<Subscription> findByUserAndTopic(User user, Topic topic);

	/**
	 * Finds a subscription by user ID and topic ID.
	 */
	Optional<Subscription> findByUserIdAndTopicId(Long userId, Long topicId);

	/**
	 * Checks if a subscription exists.
	 */
	boolean existsByUserIdAndTopicId(Long userId, Long topicId);

	/**
	 * Deletes a subscription by user ID and topic ID.
	 */
	void deleteByUserIdAndTopicId(Long userId, Long topicId);
}
