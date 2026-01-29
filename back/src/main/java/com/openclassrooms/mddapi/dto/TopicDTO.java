package com.openclassrooms.mddapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Topic entity.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicDTO {

  private Long id;
  private String name;
  private String description;
}
