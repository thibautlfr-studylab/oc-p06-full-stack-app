package com.openclassrooms.mddapi.service.implementations;

import com.openclassrooms.mddapi.dto.SubscriptionDTO;
import com.openclassrooms.mddapi.exception.AlreadySubscribedException;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.mapper.SubscriptionMapper;
import com.openclassrooms.mddapi.model.Subscription;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.service.interfaces.ISubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Subscription operations.
 */
@Service
@RequiredArgsConstructor
public class SubscriptionService implements ISubscriptionService {

  private final SubscriptionRepository subscriptionRepository;
  private final UserRepository userRepository;
  private final TopicRepository topicRepository;
  private final SubscriptionMapper subscriptionMapper;

  @Override
  public List<SubscriptionDTO> getSubscriptionsByUserId(Long userId) {
    return subscriptionMapper.toDTOList(subscriptionRepository.findByUserId(userId));
  }

  @Override
  public List<Long> getSubscribedTopicIds(Long userId) {
    return subscriptionRepository.findByUserId(userId)
        .stream()
        .map(sub -> sub.getTopic().getId())
        .collect(Collectors.toList());
  }

  @Override
  @Transactional
  public SubscriptionDTO subscribe(Long userId, Long topicId) {
    if (subscriptionRepository.existsByUserIdAndTopicId(userId, topicId)) {
      throw new AlreadySubscribedException("User is already subscribed to this topic");
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    Topic topic = topicRepository.findById(topicId)
        .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + topicId));

    Subscription subscription = Subscription.builder()
        .user(user)
        .topic(topic)
        .build();

    return subscriptionMapper.toDTO(subscriptionRepository.save(subscription));
  }

  @Override
  @Transactional
  public void unsubscribe(Long userId, Long topicId) {
    if (!subscriptionRepository.existsByUserIdAndTopicId(userId, topicId)) {
      throw new ResourceNotFoundException("Subscription not found for user " + userId + " and topic " + topicId);
    }
    subscriptionRepository.deleteByUserIdAndTopicId(userId, topicId);
  }

  @Override
  public boolean isSubscribed(Long userId, Long topicId) {
    return subscriptionRepository.existsByUserIdAndTopicId(userId, topicId);
  }
}
