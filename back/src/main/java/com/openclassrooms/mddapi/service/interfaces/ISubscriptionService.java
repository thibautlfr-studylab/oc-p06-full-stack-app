package com.openclassrooms.mddapi.service.interfaces;

import java.util.List;

import com.openclassrooms.mddapi.dto.SubscriptionDTO;

/**
 * Service interface for Subscription operations.
 */
public interface ISubscriptionService {

	/**
	 * Retrieves all subscriptions for a user.
	 *
	 * @param userId the user ID
	 * @return list of subscription DTOs
	 */
	List<SubscriptionDTO> getSubscriptionsByUserId(Long userId);

	/**
	 * Retrieves all topic IDs a user is subscribed to.
	 *
	 * @param userId the user ID
	 * @return list of topic IDs
	 */
	List<Long> getSubscribedTopicIds(Long userId);

	/**
	 * Subscribes a user to a topic.
	 *
	 * @param userId  the user ID
	 * @param topicId the topic ID
	 * @return the created subscription DTO
	 */
	SubscriptionDTO subscribe(Long userId, Long topicId);

	/**
	 * Unsubscribes a user from a topic.
	 *
	 * @param userId  the user ID
	 * @param topicId the topic ID
	 */
	void unsubscribe(Long userId, Long topicId);

	/**
	 * Checks if a user is subscribed to a topic.
	 *
	 * @param userId  the user ID
	 * @param topicId the topic ID
	 * @return true if subscribed
	 */
	boolean isSubscribed(Long userId, Long topicId);
}
