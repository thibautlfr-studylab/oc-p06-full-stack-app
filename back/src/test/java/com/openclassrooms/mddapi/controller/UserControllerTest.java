package com.openclassrooms.mddapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mddapi.dto.UserDTO;
import com.openclassrooms.mddapi.dto.request.UpdateProfileRequest;
import com.openclassrooms.mddapi.exception.GlobalExceptionHandler;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.exception.UserAlreadyExistsException;
import com.openclassrooms.mddapi.service.interfaces.IUserService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for UserController.
 */
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IUserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        userDTO = UserDTO.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("GET /api/users/{id}")
    class GetUserByIdEndpointTests {

        @Test
        @DisplayName("getUserById_ShouldReturn200_WhenUserExists")
        void getUserById_ShouldReturn200_WhenUserExists() throws Exception {
            when(userService.getUserById(1L)).thenReturn(userDTO);

            mockMvc.perform(get("/api/users/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.username").value("testuser"))
                    .andExpect(jsonPath("$.email").value("test@example.com"));
        }

        @Test
        @DisplayName("getUserById_ShouldReturn404_WhenUserNotFound")
        void getUserById_ShouldReturn404_WhenUserNotFound() throws Exception {
            when(userService.getUserById(99L))
                    .thenThrow(new ResourceNotFoundException("User not found"));

            mockMvc.perform(get("/api/users/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PUT /api/users/{id}")
    class UpdateProfileEndpointTests {

        @Test
        @DisplayName("updateProfile_ShouldReturn200_WhenValidRequest")
        void updateProfile_ShouldReturn200_WhenValidRequest() throws Exception {
            UpdateProfileRequest request = UpdateProfileRequest.builder()
                    .username("newusername")
                    .email("new@example.com")
                    .build();

            UserDTO updatedUser = UserDTO.builder()
                    .id(1L)
                    .username("newusername")
                    .email("new@example.com")
                    .build();

            when(userService.updateProfile(eq(1L), any(UpdateProfileRequest.class)))
                    .thenReturn(updatedUser);

            mockMvc.perform(put("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.username").value("newusername"))
                    .andExpect(jsonPath("$.email").value("new@example.com"));
        }

        @Test
        @DisplayName("updateProfile_ShouldReturn404_WhenUserNotFound")
        void updateProfile_ShouldReturn404_WhenUserNotFound() throws Exception {
            UpdateProfileRequest request = UpdateProfileRequest.builder()
                    .username("newusername")
                    .build();

            when(userService.updateProfile(eq(99L), any(UpdateProfileRequest.class)))
                    .thenThrow(new ResourceNotFoundException("User not found"));

            mockMvc.perform(put("/api/users/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("updateProfile_ShouldReturn409_WhenUsernameExists")
        void updateProfile_ShouldReturn409_WhenUsernameExists() throws Exception {
            UpdateProfileRequest request = UpdateProfileRequest.builder()
                    .username("existinguser")
                    .build();

            when(userService.updateProfile(eq(1L), any(UpdateProfileRequest.class)))
                    .thenThrow(new UserAlreadyExistsException("Username already exists"));

            mockMvc.perform(put("/api/users/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }
}
