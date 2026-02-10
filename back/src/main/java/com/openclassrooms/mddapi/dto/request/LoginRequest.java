package com.openclassrooms.mddapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user login requests.
 * The identifier can be either an email or a username.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

  @NotBlank(message = "Email or username is required")
  private String identifier;

  @NotBlank(message = "Password is required")
  private String password;
}
