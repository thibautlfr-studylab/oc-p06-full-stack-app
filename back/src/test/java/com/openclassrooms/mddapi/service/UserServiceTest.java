package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.UserDTO;
import com.openclassrooms.mddapi.dto.request.UpdateProfileRequest;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.exception.UserAlreadyExistsException;
import com.openclassrooms.mddapi.mapper.UserMapper;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.service.implementations.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDTO testUserDTO;

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

        testUserDTO = UserDTO.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .createdAt(testUser.getCreatedAt())
                .updatedAt(testUser.getUpdatedAt())
                .build();
    }

    @Nested
    @DisplayName("GetUserById Tests")
    class GetUserByIdTests {

        @Test
        @DisplayName("getUserById_ShouldReturnUserDTO_WhenUserExists")
        void getUserById_ShouldReturnUserDTO_WhenUserExists() {
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userMapper.toDTO(testUser)).thenReturn(testUserDTO);

            UserDTO result = userService.getUserById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getUsername()).isEqualTo("testuser");
            assertThat(result.getEmail()).isEqualTo("test@example.com");

            verify(userRepository).findById(1L);
            verify(userMapper).toDTO(testUser);
        }

        @Test
        @DisplayName("getUserById_ShouldThrowResourceNotFoundException_WhenUserNotFound")
        void getUserById_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("User not found");

            verify(userRepository).findById(99L);
            verify(userMapper, never()).toDTO(any(User.class));
        }
    }

    @Nested
    @DisplayName("UpdateProfile Tests")
    class UpdateProfileTests {

        @Test
        @DisplayName("updateProfile_ShouldUpdateUsername_WhenUsernameProvided")
        void updateProfile_ShouldUpdateUsername_WhenUsernameProvided() {
            UpdateProfileRequest request = UpdateProfileRequest.builder()
                    .username("newusername")
                    .build();

            User updatedUser = User.builder()
                    .id(1L)
                    .username("newusername")
                    .email("test@example.com")
                    .password("encodedPassword")
                    .build();

            UserDTO updatedDTO = UserDTO.builder()
                    .id(1L)
                    .username("newusername")
                    .email("test@example.com")
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByUsername("newusername")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(updatedUser);
            when(userMapper.toDTO(any(User.class))).thenReturn(updatedDTO);

            UserDTO result = userService.updateProfile(1L, request);

            assertThat(result.getUsername()).isEqualTo("newusername");
            verify(userRepository).existsByUsername("newusername");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("updateProfile_ShouldUpdateEmail_WhenEmailProvided")
        void updateProfile_ShouldUpdateEmail_WhenEmailProvided() {
            UpdateProfileRequest request = UpdateProfileRequest.builder()
                    .email("new@example.com")
                    .build();

            UserDTO updatedDTO = UserDTO.builder()
                    .id(1L)
                    .username("testuser")
                    .email("new@example.com")
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(userMapper.toDTO(any(User.class))).thenReturn(updatedDTO);

            UserDTO result = userService.updateProfile(1L, request);

            assertThat(result.getEmail()).isEqualTo("new@example.com");
            verify(userRepository).existsByEmail("new@example.com");
        }

        @Test
        @DisplayName("updateProfile_ShouldUpdatePassword_WhenPasswordProvided")
        void updateProfile_ShouldUpdatePassword_WhenPasswordProvided() {
            UpdateProfileRequest request = UpdateProfileRequest.builder()
                    .password("NewPassword1!")
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(passwordEncoder.encode("NewPassword1!")).thenReturn("newEncodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(userMapper.toDTO(any(User.class))).thenReturn(testUserDTO);

            userService.updateProfile(1L, request);

            verify(passwordEncoder).encode("NewPassword1!");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("updateProfile_ShouldThrowUserAlreadyExistsException_WhenUsernameExists")
        void updateProfile_ShouldThrowUserAlreadyExistsException_WhenUsernameExists() {
            UpdateProfileRequest request = UpdateProfileRequest.builder()
                    .username("existinguser")
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByUsername("existinguser")).thenReturn(true);

            assertThatThrownBy(() -> userService.updateProfile(1L, request))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessage("Username already exists");

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("updateProfile_ShouldThrowUserAlreadyExistsException_WhenEmailExists")
        void updateProfile_ShouldThrowUserAlreadyExistsException_WhenEmailExists() {
            UpdateProfileRequest request = UpdateProfileRequest.builder()
                    .email("existing@example.com")
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

            assertThatThrownBy(() -> userService.updateProfile(1L, request))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessage("Email already exists");

            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("updateProfile_ShouldThrowResourceNotFoundException_WhenUserNotFound")
        void updateProfile_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
            UpdateProfileRequest request = UpdateProfileRequest.builder()
                    .username("newusername")
                    .build();

            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateProfile(99L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("User not found");
        }

        @Test
        @DisplayName("updateProfile_ShouldNotCheckUsername_WhenSameUsername")
        void updateProfile_ShouldNotCheckUsername_WhenSameUsername() {
            UpdateProfileRequest request = UpdateProfileRequest.builder()
                    .username("testuser")  // Same as current username
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(userMapper.toDTO(any(User.class))).thenReturn(testUserDTO);

            userService.updateProfile(1L, request);

            verify(userRepository, never()).existsByUsername(anyString());
        }

        @Test
        @DisplayName("updateProfile_ShouldNotCheckEmail_WhenSameEmail")
        void updateProfile_ShouldNotCheckEmail_WhenSameEmail() {
            UpdateProfileRequest request = UpdateProfileRequest.builder()
                    .email("test@example.com")  // Same as current email
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(userRepository.save(any(User.class))).thenReturn(testUser);
            when(userMapper.toDTO(any(User.class))).thenReturn(testUserDTO);

            userService.updateProfile(1L, request);

            verify(userRepository, never()).existsByEmail(anyString());
        }
    }
}
