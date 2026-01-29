package com.openclassrooms.mddapi.exception;

/**
 * Exception thrown when a user attempts to subscribe to a topic
 * they are already subscribed to.
 */
public class AlreadySubscribedException extends RuntimeException {

	public AlreadySubscribedException(String message) {
		super(message);
	}
}
