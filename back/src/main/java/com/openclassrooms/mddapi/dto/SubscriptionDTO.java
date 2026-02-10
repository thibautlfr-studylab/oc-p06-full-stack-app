package com.openclassrooms.mddapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Subscription entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDTO {

  private Long id;
  private Long userId;
  private Long topicId;
  private String topicName;
  private String topicDescription;
  private LocalDateTime subscribedAt;
}
