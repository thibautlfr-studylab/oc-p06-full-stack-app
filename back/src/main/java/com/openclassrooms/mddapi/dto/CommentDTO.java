package com.openclassrooms.mddapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Comment entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {

  private Long id;
  private String content;
  private Long authorId;
  private String authorUsername;
  private Long postId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
