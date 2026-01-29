package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.SubscriptionDTO;
import com.openclassrooms.mddapi.dto.request.SubscribeRequest;
import com.openclassrooms.mddapi.dto.response.MessageResponse;
import com.openclassrooms.mddapi.service.interfaces.ISubscriptionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for Subscription operations.
 */
@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

  private final ISubscriptionService subscriptionService;

  /**
   * Retrieves all subscriptions for a user.
   *
   * @param userId the user ID
   * @return list of subscription DTOs
   */
  @GetMapping("/user/{userId}")
  public ResponseEntity<List<SubscriptionDTO>> getUserSubscriptions(@PathVariable Long userId) {
    return ResponseEntity.ok(subscriptionService.getSubscriptionsByUserId(userId));
  }

  /**
   * Retrieves all topic IDs a user is subscribed to.
   *
   * @param userId the user ID
   * @return list of topic IDs
   */
  @GetMapping("/user/{userId}/topic-ids")
  public ResponseEntity<List<Long>> getSubscribedTopicIds(@PathVariable Long userId) {
    return ResponseEntity.ok(subscriptionService.getSubscribedTopicIds(userId));
  }

  /**
   * Subscribes a user to a topic.
   *
   * @param request the subscribe request containing userId and topicId
   * @return the created subscription DTO
   */
  @PostMapping
  public ResponseEntity<SubscriptionDTO> subscribe(@Valid @RequestBody SubscribeRequest request) {
    return ResponseEntity.ok(subscriptionService.subscribe(request.getUserId(), request.getTopicId()));
  }

  /**
   * Unsubscribes a user from a topic.
   *
   * @param userId  the user ID
   * @param topicId the topic ID
   * @return success message
   */
  @DeleteMapping("/user/{userId}/topic/{topicId}")
  public ResponseEntity<MessageResponse> unsubscribe(
      @PathVariable Long userId,
      @PathVariable Long topicId) {
    subscriptionService.unsubscribe(userId, topicId);
    return ResponseEntity.ok(MessageResponse.builder()
        .message("Successfully unsubscribed")
        .build());
  }
}
