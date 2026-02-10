package com.openclassrooms.mddapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

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
