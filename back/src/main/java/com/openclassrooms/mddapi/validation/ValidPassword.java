package com.openclassrooms.mddapi.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Custom validation annotation for password strength.
 * Password must contain at least 8 characters, 1 digit, 1 lowercase,
 * 1 uppercase, and 1 special character.
 */
@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {

	String message() default "Password must contain at least 8 characters, 1 digit, 1 lowercase, 1 uppercase, and 1 special character";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
