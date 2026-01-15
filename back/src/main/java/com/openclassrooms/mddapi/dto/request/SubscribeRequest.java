package com.openclassrooms.mddapi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for subscription requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscribeRequest {

	@NotNull(message = "User ID is required")
	private Long userId;

	@NotNull(message = "Topic ID is required")
	private Long topicId;
}
