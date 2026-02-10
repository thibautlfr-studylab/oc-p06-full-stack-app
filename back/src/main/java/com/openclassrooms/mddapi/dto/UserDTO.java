package com.openclassrooms.mddapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for User entity (without sensitive data like password).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

  private Long id;
  private String email;
  private String username;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
