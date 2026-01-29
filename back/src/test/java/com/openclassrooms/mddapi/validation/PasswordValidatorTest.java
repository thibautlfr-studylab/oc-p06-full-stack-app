package com.openclassrooms.mddapi.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for PasswordValidator.
 */
class PasswordValidatorTest {

    private PasswordValidator passwordValidator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        passwordValidator = new PasswordValidator();
        context = mock(ConstraintValidatorContext.class);
    }

    @Nested
    @DisplayName("Valid Password Tests")
    class ValidPasswordTests {

        @ParameterizedTest
        @DisplayName("isValid_ShouldReturnTrue_WhenPasswordMeetsAllCriteria")
        @ValueSource(strings = {
                "Password1!",
                "Abcdefg1@",
                "Test1234#",
                "MyP@ssw0rd",
                "Str0ng!Pass",
                "C0mpl3x@Pass",
                "V@lidP4ss",
                "Secur3!Password"
        })
        void isValid_ShouldReturnTrue_WhenPasswordMeetsAllCriteria(String password) {
            boolean result = passwordValidator.isValid(password, context);
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("Invalid Password Tests - Length")
    class InvalidPasswordLengthTests {

        @Test
        @DisplayName("isValid_ShouldReturnFalse_WhenPasswordTooShort")
        void isValid_ShouldReturnFalse_WhenPasswordTooShort() {
            String password = "Pass1!a";  // Only 7 characters

            boolean result = passwordValidator.isValid(password, context);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("isValid_ShouldReturnTrue_WhenPasswordExactlyMinLength")
        void isValid_ShouldReturnTrue_WhenPasswordExactlyMinLength() {
            String password = "Pass1!ab";  // Exactly 8 characters

            boolean result = passwordValidator.isValid(password, context);

            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("Invalid Password Tests - Missing Digit")
    class InvalidPasswordMissingDigitTests {

        @Test
        @DisplayName("isValid_ShouldReturnFalse_WhenMissingDigit")
        void isValid_ShouldReturnFalse_WhenMissingDigit() {
            String password = "Password!";  // No digit

            boolean result = passwordValidator.isValid(password, context);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Invalid Password Tests - Missing Lowercase")
    class InvalidPasswordMissingLowercaseTests {

        @Test
        @DisplayName("isValid_ShouldReturnFalse_WhenMissingLowercase")
        void isValid_ShouldReturnFalse_WhenMissingLowercase() {
            String password = "PASSWORD1!";  // No lowercase

            boolean result = passwordValidator.isValid(password, context);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Invalid Password Tests - Missing Uppercase")
    class InvalidPasswordMissingUppercaseTests {

        @Test
        @DisplayName("isValid_ShouldReturnFalse_WhenMissingUppercase")
        void isValid_ShouldReturnFalse_WhenMissingUppercase() {
            String password = "password1!";  // No uppercase

            boolean result = passwordValidator.isValid(password, context);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Invalid Password Tests - Missing Special Character")
    class InvalidPasswordMissingSpecialTests {

        @Test
        @DisplayName("isValid_ShouldReturnFalse_WhenMissingSpecialCharacter")
        void isValid_ShouldReturnFalse_WhenMissingSpecialCharacter() {
            String password = "Password1";  // No special character

            boolean result = passwordValidator.isValid(password, context);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Invalid Password Tests - Contains Whitespace")
    class InvalidPasswordContainsWhitespaceTests {

        @Test
        @DisplayName("isValid_ShouldReturnFalse_WhenPasswordContainsSpace")
        void isValid_ShouldReturnFalse_WhenPasswordContainsSpace() {
            String password = "Pass word1!";  // Contains space

            boolean result = passwordValidator.isValid(password, context);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Null and Empty Password Tests")
    class NullAndEmptyPasswordTests {

        @Test
        @DisplayName("isValid_ShouldReturnTrue_WhenPasswordIsNull")
        void isValid_ShouldReturnTrue_WhenPasswordIsNull() {
            // Let @NotBlank handle null validation
            boolean result = passwordValidator.isValid(null, context);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("isValid_ShouldReturnTrue_WhenPasswordIsEmpty")
        void isValid_ShouldReturnTrue_WhenPasswordIsEmpty() {
            // Let @NotBlank handle empty validation
            boolean result = passwordValidator.isValid("", context);

            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("Special Characters Tests")
    class SpecialCharactersTests {

        @ParameterizedTest
        @DisplayName("isValid_ShouldAcceptDifferentSpecialCharacters")
        @ValueSource(strings = {
                "Password1@",
                "Password1#",
                "Password1$",
                "Password1%",
                "Password1^",
                "Password1&",
                "Password1+",
                "Password1=",
                "Password1!"
        })
        void isValid_ShouldAcceptDifferentSpecialCharacters(String password) {
            boolean result = passwordValidator.isValid(password, context);
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("isValid_ShouldReturnFalse_WhenSpecialCharacterNotInAllowedSet")
        void isValid_ShouldReturnFalse_WhenSpecialCharacterNotInAllowedSet() {
            String password = "Password1*";  // * is not in the allowed set

            boolean result = passwordValidator.isValid(password, context);

            assertThat(result).isFalse();
        }
    }
}
