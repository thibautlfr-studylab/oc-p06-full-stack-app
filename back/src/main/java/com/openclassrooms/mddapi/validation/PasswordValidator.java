package com.openclassrooms.mddapi.validation;

import java.util.regex.Pattern;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator for password strength.
 * Validates that password meets security requirements:
 * - At least 8 characters
 * - At least 1 digit
 * - At least 1 lowercase letter
 * - At least 1 uppercase letter
 * - At least 1 special character (@#$%^&+=!)
 */
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

	private static final Pattern PASSWORD_PATTERN = Pattern.compile(
			"^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$"
	);

	@Override
	public void initialize(ValidPassword constraintAnnotation) {
		// No initialization needed
	}

	@Override
	public boolean isValid(String password, ConstraintValidatorContext context) {
		if (password == null || password.isEmpty()) {
			return true; // Let @NotBlank handle null/empty validation
		}
		return PASSWORD_PATTERN.matcher(password).matches();
	}
}
