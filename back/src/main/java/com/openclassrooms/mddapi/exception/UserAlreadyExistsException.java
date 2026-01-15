package com.openclassrooms.mddapi.exception;

/**
 * Exception thrown when attempting to create a user with an email or username that already exists.
 */
public class UserAlreadyExistsException extends RuntimeException {

	public UserAlreadyExistsException(String message) {
		super(message);
	}
}
