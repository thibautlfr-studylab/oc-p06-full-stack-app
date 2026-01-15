package com.openclassrooms.mddapi.service.implementations;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.dto.request.LoginRequest;
import com.openclassrooms.mddapi.dto.request.RegisterRequest;
import com.openclassrooms.mddapi.dto.response.AuthResponse;
import com.openclassrooms.mddapi.exception.InvalidCredentialsException;
import com.openclassrooms.mddapi.exception.UserAlreadyExistsException;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.service.interfaces.IAuthService;

import lombok.RequiredArgsConstructor;

/**
 * Service implementation for authentication operations.
 */
@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public AuthResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new UserAlreadyExistsException("Email already exists");
		}

		if (userRepository.existsByUsername(request.getUsername())) {
			throw new UserAlreadyExistsException("Username already exists");
		}

		User user = User.builder()
				.username(request.getUsername())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.build();

		User savedUser = userRepository.save(user);

		return AuthResponse.builder()
				.id(savedUser.getId())
				.username(savedUser.getUsername())
				.email(savedUser.getEmail())
				.message("User registered successfully")
				.build();
	}

	@Override
	public AuthResponse login(LoginRequest request) {
		User user = userRepository.findByEmailOrUsername(request.getIdentifier(), request.getIdentifier())
				.orElseThrow(() -> new InvalidCredentialsException("Invalid email/username or password"));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Invalid email/username or password");
		}

		return AuthResponse.builder()
				.id(user.getId())
				.username(user.getUsername())
				.email(user.getEmail())
				.message("Login successful")
				.build();
	}
}
