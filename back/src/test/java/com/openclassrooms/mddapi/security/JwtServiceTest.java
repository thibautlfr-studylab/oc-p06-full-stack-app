package com.openclassrooms.mddapi.security;

import com.openclassrooms.mddapi.model.User;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for JwtService.
 */
class JwtServiceTest {

    private JwtService jwtService;
    private User testUser;

    // Valid Base64-encoded secret key (at least 256 bits)
    private static final String SECRET_KEY = "TUREMjAyNFNlY3JldEtleUZvckpXVEF1dGhlbnRpY2F0aW9uTXVzdEJlQXRMZWFzdDI1NkJpdHNMb25nIQ==";
    private static final long EXPIRATION = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", SECRET_KEY);
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", EXPIRATION);

        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .build();
    }

    @Nested
    @DisplayName("GenerateToken Tests")
    class GenerateTokenTests {

        @Test
        @DisplayName("generateToken_ShouldReturnValidToken_WhenUserProvided")
        void generateToken_ShouldReturnValidToken_WhenUserProvided() {
            String token = jwtService.generateToken(testUser);

            assertThat(token).isNotNull();
            assertThat(token).isNotBlank();
            assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
        }

        @Test
        @DisplayName("generateToken_ShouldIncludeUserIdClaim_WhenUserProvided")
        void generateToken_ShouldIncludeUserIdClaim_WhenUserProvided() {
            String token = jwtService.generateToken(testUser);

            Long userId = jwtService.extractUserId(token);
            assertThat(userId).isEqualTo(1L);
        }

        @Test
        @DisplayName("generateToken_ShouldIncludeEmailAsSubject_WhenUserProvided")
        void generateToken_ShouldIncludeEmailAsSubject_WhenUserProvided() {
            String token = jwtService.generateToken(testUser);

            String username = jwtService.extractUsername(token);
            assertThat(username).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("generateToken_ShouldWorkWithExtraClaims_WhenMapProvided")
        void generateToken_ShouldWorkWithExtraClaims_WhenMapProvided() {
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("customClaim", "customValue");

            String token = jwtService.generateToken(extraClaims, "subject@example.com");

            assertThat(token).isNotNull();
            String subject = jwtService.extractUsername(token);
            assertThat(subject).isEqualTo("subject@example.com");
        }
    }

    @Nested
    @DisplayName("ExtractUsername Tests")
    class ExtractUsernameTests {

        @Test
        @DisplayName("extractUsername_ShouldReturnEmail_WhenValidToken")
        void extractUsername_ShouldReturnEmail_WhenValidToken() {
            String token = jwtService.generateToken(testUser);

            String username = jwtService.extractUsername(token);

            assertThat(username).isEqualTo("test@example.com");
        }
    }

    @Nested
    @DisplayName("ExtractUserId Tests")
    class ExtractUserIdTests {

        @Test
        @DisplayName("extractUserId_ShouldReturnUserId_WhenValidToken")
        void extractUserId_ShouldReturnUserId_WhenValidToken() {
            String token = jwtService.generateToken(testUser);

            Long userId = jwtService.extractUserId(token);

            assertThat(userId).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("IsTokenValid Tests")
    class IsTokenValidTests {

        @Test
        @DisplayName("isTokenValid_ShouldReturnTrue_WhenValidTokenAndMatchingUser")
        void isTokenValid_ShouldReturnTrue_WhenValidTokenAndMatchingUser() {
            String token = jwtService.generateToken(testUser);

            UserDetails userDetails = mock(UserDetails.class);
            when(userDetails.getUsername()).thenReturn("test@example.com");

            boolean isValid = jwtService.isTokenValid(token, userDetails);

            assertThat(isValid).isTrue();
        }

        @Test
        @DisplayName("isTokenValid_ShouldReturnFalse_WhenUsernameMismatch")
        void isTokenValid_ShouldReturnFalse_WhenUsernameMismatch() {
            String token = jwtService.generateToken(testUser);

            UserDetails userDetails = mock(UserDetails.class);
            when(userDetails.getUsername()).thenReturn("different@example.com");

            boolean isValid = jwtService.isTokenValid(token, userDetails);

            assertThat(isValid).isFalse();
        }

        @Test
        @DisplayName("isTokenValid_ShouldThrowException_WhenTokenExpired")
        void isTokenValid_ShouldThrowException_WhenTokenExpired() {
            // Create a JwtService with very short expiration for testing
            JwtService shortExpirationService = new JwtService();
            ReflectionTestUtils.setField(shortExpirationService, "secretKey", SECRET_KEY);
            ReflectionTestUtils.setField(shortExpirationService, "jwtExpiration", 1L); // 1ms expiration

            String token = shortExpirationService.generateToken(testUser);

            // Wait for token to expire
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            UserDetails userDetails = mock(UserDetails.class);
            when(userDetails.getUsername()).thenReturn("test@example.com");

            assertThatThrownBy(() -> shortExpirationService.isTokenValid(token, userDetails))
                    .isInstanceOf(ExpiredJwtException.class);
        }
    }

    @Nested
    @DisplayName("ExtractClaim Tests")
    class ExtractClaimTests {

        @Test
        @DisplayName("extractClaim_ShouldExtractCustomClaim_WhenClaimExists")
        void extractClaim_ShouldExtractCustomClaim_WhenClaimExists() {
            String token = jwtService.generateToken(testUser);

            String username = jwtService.extractClaim(token, claims -> claims.get("username", String.class));

            assertThat(username).isEqualTo("testuser");
        }
    }
}
