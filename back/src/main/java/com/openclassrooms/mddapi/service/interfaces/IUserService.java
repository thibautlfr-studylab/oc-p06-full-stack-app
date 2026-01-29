package com.openclassrooms.mddapi.service.interfaces;

import com.openclassrooms.mddapi.dto.UserDTO;
import com.openclassrooms.mddapi.dto.request.UpdateProfileRequest;

/**
 * Service interface for user operations.
 */
public interface IUserService {

  /**
   * Retrieves a user by their ID.
   *
   * @param id the user ID
   * @return the user DTO
   */
  UserDTO getUserById(Long id);

  /**
   * Updates a user's profile.
   *
   * @param id      the user ID
   * @param request the update profile request
   * @return the updated user DTO
   */
  UserDTO updateProfile(Long id, UpdateProfileRequest request);
}
