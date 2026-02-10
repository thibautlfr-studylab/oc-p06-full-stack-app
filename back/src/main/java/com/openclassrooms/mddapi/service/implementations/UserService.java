package com.openclassrooms.mddapi.service.implementations;

import com.openclassrooms.mddapi.dto.UserDTO;
import com.openclassrooms.mddapi.dto.request.UpdateProfileRequest;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.exception.UserAlreadyExistsException;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service implementation for user operations.
 */
@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDTO getUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    return userMapper.toDTO(user);
  }

  @Override
  public UserDTO updateProfile(Long id, UpdateProfileRequest request) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
      if (userRepository.existsByUsername(request.getUsername())) {
        throw new UserAlreadyExistsException("Username already exists");
      }
      user.setUsername(request.getUsername());
    }

    if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
      if (userRepository.existsByEmail(request.getEmail())) {
        throw new UserAlreadyExistsException("Email already exists");
      }
      user.setEmail(request.getEmail());
    }

    if (request.getPassword() != null && !request.getPassword().isEmpty()) {
      user.setPassword(passwordEncoder.encode(request.getPassword()));
    }

    User savedUser = userRepository.save(user);
    return userMapper.toDTO(savedUser);
  }
}
