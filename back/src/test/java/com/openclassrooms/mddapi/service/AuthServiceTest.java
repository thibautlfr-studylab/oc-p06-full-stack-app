package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.UserDTO;
import com.openclassrooms.mddapi.dto.request.LoginRequest;
import com.openclassrooms.mddapi.dto.request.RegisterRequest;
import com.openclassrooms.mddapi.dto.response.AuthResponse;
import com.openclassrooms.mddapi.exception.InvalidCredentialsException;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.exception.UserAlreadyExistsException;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.security.JwtService;
import com.openclassrooms.mddapi.service.implementations.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthService.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        registerRequest = RegisterRequest.builder()
                .username("newuser")
                .email("new@example.com")
                .password("Password1!")
                .build();

        loginRequest = LoginRequest.builder()
                .identifier("test@example.com")
                .password("Password1!")
                .build();
    }

    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {

        @Test
        @DisplayName("register_ShouldReturnAuthResponse_WhenValidRequest")
        void register_ShouldReturnAuthResponse_WhenValidRequest() {
            when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
            when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
            when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(1L);
                return user;
            });
            when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

            AuthResponse response = authService.register(registerRequest);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getUsername()).isEqualTo("newuser");
            assertThat(response.getEmail()).isEqualTo("new@example.com");
            assertThat(response.getToken()).isEqualTo("jwt-token");
            assertThat(response.getMessage()).isEqualTo("User registered successfully");

            verify(userRepository).existsByEmail(registerRequest.getEmail());
            verify(userRepository).existsByUsername(registerRequest.getUsername());
            verify(userRepository).save(any(User.class));
            verify(jwtService).generateToken(any(User.class));
        }

        @Test
        @DisplayName("register_ShouldThrowUserAlreadyExistsException_WhenEmailExists")
        void register_ShouldThrowUserAlreadyExistsException_WhenEmailExists() {
            when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

            assertThatThrownBy(() -> authService.register(registerRequest))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessage("Email already exists");

            verify(userRepository).existsByEmail(registerRequest.getEmail());
            verify(userRepository, never()).existsByUsername(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("register_ShouldThrowUserAlreadyExistsException_WhenUsernameExists")
        void register_ShouldThrowUserAlreadyExistsException_WhenUsernameExists() {
            when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
            when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

            assertThatThrownBy(() -> authService.register(registerRequest))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessage("Username already exists");

            verify(userRepository).existsByEmail(registerRequest.getEmail());
            verify(userRepository).existsByUsername(registerRequest.getUsername());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("login_ShouldReturnAuthResponse_WhenValidCredentials")
        void login_ShouldReturnAuthResponse_WhenValidCredentials() {
            when(userRepository.findByEmailOrUsername(loginRequest.getIdentifier(), loginRequest.getIdentifier()))
                    .thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).thenReturn(true);
            when(jwtService.generateToken(testUser)).thenReturn("jwt-token");

            AuthResponse response = authService.login(loginRequest);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(testUser.getId());
            assertThat(response.getUsername()).isEqualTo(testUser.getUsername());
            assertThat(response.getEmail()).isEqualTo(testUser.getEmail());
            assertThat(response.getToken()).isEqualTo("jwt-token");
            assertThat(response.getMessage()).isEqualTo("Login successful");
        }

        @Test
        @DisplayName("login_ShouldThrowInvalidCredentialsException_WhenUserNotFound")
        void login_ShouldThrowInvalidCredentialsException_WhenUserNotFound() {
            when(userRepository.findByEmailOrUsername(loginRequest.getIdentifier(), loginRequest.getIdentifier()))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(InvalidCredentialsException.class)
                    .hasMessage("Invalid email/username or password");

            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("login_ShouldThrowInvalidCredentialsException_WhenWrongPassword")
        void login_ShouldThrowInvalidCredentialsException_WhenWrongPassword() {
            when(userRepository.findByEmailOrUsername(loginRequest.getIdentifier(), loginRequest.getIdentifier()))
                    .thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(loginRequest.getPassword(), testUser.getPassword())).thenReturn(false);

            assertThatThrownBy(() -> authService.login(loginRequest))
                    .isInstanceOf(InvalidCredentialsException.class)
                    .hasMessage("Invalid email/username or password");

            verify(jwtService, never()).generateToken(any(User.class));
        }

        @Test
        @DisplayName("login_ShouldWorkWithUsername_WhenUsernameProvided")
        void login_ShouldWorkWithUsername_WhenUsernameProvided() {
            LoginRequest usernameLogin = LoginRequest.builder()
                    .identifier("testuser")
                    .password("Password1!")
                    .build();

            when(userRepository.findByEmailOrUsername("testuser", "testuser"))
                    .thenReturn(Optional.of(testUser));
            when(passwordEncoder.matches(usernameLogin.getPassword(), testUser.getPassword())).thenReturn(true);
            when(jwtService.generateToken(testUser)).thenReturn("jwt-token");

            AuthResponse response = authService.login(usernameLogin);

            assertThat(response).isNotNull();
            assertThat(response.getUsername()).isEqualTo(testUser.getUsername());
        }
    }

    @Nested
    @DisplayName("GetCurrentUser Tests")
    class GetCurrentUserTests {

        @Test
        @DisplayName("getCurrentUser_ShouldReturnUserDTO_WhenAuthenticated")
        void getCurrentUser_ShouldReturnUserDTO_WhenAuthenticated() {
            SecurityContextHolder.setContext(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("test@example.com");
            when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));

            UserDTO result = authService.getCurrentUser();

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testUser.getId());
            assertThat(result.getUsername()).isEqualTo(testUser.getUsername());
            assertThat(result.getEmail()).isEqualTo(testUser.getEmail());
            assertThat(result.getCreatedAt()).isEqualTo(testUser.getCreatedAt());
            assertThat(result.getUpdatedAt()).isEqualTo(testUser.getUpdatedAt());
        }

        @Test
        @DisplayName("getCurrentUser_ShouldThrowResourceNotFoundException_WhenUserNotFound")
        void getCurrentUser_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
            SecurityContextHolder.setContext(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("unknown@example.com");
            when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.getCurrentUser())
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("User not found");
        }
    }
}
