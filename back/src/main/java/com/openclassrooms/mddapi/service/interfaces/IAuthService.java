package com.openclassrooms.mddapi.service.interfaces;

import com.openclassrooms.mddapi.dto.request.LoginRequest;
import com.openclassrooms.mddapi.dto.request.RegisterRequest;
import com.openclassrooms.mddapi.dto.response.AuthResponse;

/**
 * Service interface for authentication operations.
 */
public interface IAuthService {

	/**
	 * Registers a new user.
	 *
	 * @param request the registration request containing user details
	 * @return the authentication response with user info
	 */
	AuthResponse register(RegisterRequest request);

	/**
	 * Authenticates a user.
	 *
	 * @param request the login request containing credentials
	 * @return the authentication response with user info
	 */
	AuthResponse login(LoginRequest request);
}
