package com.openclassrooms.mddapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mddapi.dto.UserDTO;
import com.openclassrooms.mddapi.dto.request.LoginRequest;
import com.openclassrooms.mddapi.dto.request.RegisterRequest;
import com.openclassrooms.mddapi.dto.response.AuthResponse;
import com.openclassrooms.mddapi.exception.GlobalExceptionHandler;
import com.openclassrooms.mddapi.exception.InvalidCredentialsException;
import com.openclassrooms.mddapi.exception.UserAlreadyExistsException;
import com.openclassrooms.mddapi.service.interfaces.IAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AuthController.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IAuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        registerRequest = RegisterRequest.builder()
                .username("testuser")
                .email("test@example.com")
                .password("Password1!")
                .build();

        loginRequest = LoginRequest.builder()
                .identifier("test@example.com")
                .password("Password1!")
                .build();

        authResponse = AuthResponse.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .token("jwt-token")
                .message("Success")
                .build();

        userDTO = UserDTO.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("POST /api/auth/register")
    class RegisterEndpointTests {

        @Test
        @DisplayName("register_ShouldReturn200_WhenValidRequest")
        void register_ShouldReturn200_WhenValidRequest() throws Exception {
            when(authService.register(any(RegisterRequest.class))).thenReturn(authResponse);

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.username").value("testuser"))
                    .andExpect(jsonPath("$.email").value("test@example.com"))
                    .andExpect(jsonPath("$.token").value("jwt-token"));
        }

        @Test
        @DisplayName("register_ShouldReturn409_WhenEmailExists")
        void register_ShouldReturn409_WhenEmailExists() throws Exception {
            when(authService.register(any(RegisterRequest.class)))
                    .thenThrow(new UserAlreadyExistsException("Email already exists"));

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("POST /api/auth/login")
    class LoginEndpointTests {

        @Test
        @DisplayName("login_ShouldReturn200_WhenValidCredentials")
        void login_ShouldReturn200_WhenValidCredentials() throws Exception {
            when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.token").value("jwt-token"));
        }

        @Test
        @DisplayName("login_ShouldReturn401_WhenInvalidCredentials")
        void login_ShouldReturn401_WhenInvalidCredentials() throws Exception {
            when(authService.login(any(LoginRequest.class)))
                    .thenThrow(new InvalidCredentialsException("Invalid credentials"));

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("GET /api/auth/me")
    class GetCurrentUserEndpointTests {

        @Test
        @DisplayName("getCurrentUser_ShouldReturn200_WhenAuthenticated")
        void getCurrentUser_ShouldReturn200_WhenAuthenticated() throws Exception {
            when(authService.getCurrentUser()).thenReturn(userDTO);

            mockMvc.perform(get("/api/auth/me"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.username").value("testuser"))
                    .andExpect(jsonPath("$.email").value("test@example.com"));
        }
    }
}
