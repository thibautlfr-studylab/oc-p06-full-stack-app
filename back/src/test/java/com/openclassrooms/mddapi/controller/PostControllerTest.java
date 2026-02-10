package com.openclassrooms.mddapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mddapi.dto.PostDTO;
import com.openclassrooms.mddapi.dto.request.CreatePostRequest;
import com.openclassrooms.mddapi.exception.GlobalExceptionHandler;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.service.interfaces.IPostService;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for PostController.
 */
@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IPostService postService;

    @InjectMocks
    private PostController postController;

    private ObjectMapper objectMapper;
    private PostDTO postDTO1;
    private PostDTO postDTO2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        postDTO1 = PostDTO.builder()
                .id(1L)
                .title("First Post")
                .content("This is the first post content")
                .authorId(1L)
                .authorUsername("testuser")
                .topicId(1L)
                .topicName("Java")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        postDTO2 = PostDTO.builder()
                .id(2L)
                .title("Second Post")
                .content("This is the second post content")
                .authorId(1L)
                .authorUsername("testuser")
                .topicId(2L)
                .topicName("Python")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("GET /api/posts/feed/{userId}")
    class GetFeedEndpointTests {

        @Test
        @DisplayName("getFeed_ShouldReturn200_WhenFeedExists")
        void getFeed_ShouldReturn200_WhenFeedExists() throws Exception {
            List<PostDTO> feed = Arrays.asList(postDTO1, postDTO2);
            when(postService.getFeedForUser(1L, false)).thenReturn(feed);

            mockMvc.perform(get("/api/posts/feed/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].title").value("First Post"))
                    .andExpect(jsonPath("$[1].title").value("Second Post"));
        }

        @Test
        @DisplayName("getFeed_ShouldReturn200WithEmptyArray_WhenNoSubscriptions")
        void getFeed_ShouldReturn200WithEmptyArray_WhenNoSubscriptions() throws Exception {
            when(postService.getFeedForUser(1L, false)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/posts/feed/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("getFeed_ShouldPassAscendingParameter_WhenProvided")
        void getFeed_ShouldPassAscendingParameter_WhenProvided() throws Exception {
            List<PostDTO> feed = Arrays.asList(postDTO2, postDTO1);
            when(postService.getFeedForUser(1L, true)).thenReturn(feed);

            mockMvc.perform(get("/api/posts/feed/1")
                            .param("ascending", "true"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2));
        }
    }

    @Nested
    @DisplayName("GET /api/posts/{id}")
    class GetPostByIdEndpointTests {

        @Test
        @DisplayName("getPostById_ShouldReturn200_WhenPostExists")
        void getPostById_ShouldReturn200_WhenPostExists() throws Exception {
            when(postService.getPostById(1L)).thenReturn(postDTO1);

            mockMvc.perform(get("/api/posts/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.title").value("First Post"))
                    .andExpect(jsonPath("$.content").value("This is the first post content"))
                    .andExpect(jsonPath("$.authorUsername").value("testuser"))
                    .andExpect(jsonPath("$.topicName").value("Java"));
        }

        @Test
        @DisplayName("getPostById_ShouldReturn404_WhenPostNotFound")
        void getPostById_ShouldReturn404_WhenPostNotFound() throws Exception {
            when(postService.getPostById(99L))
                    .thenThrow(new ResourceNotFoundException("Post not found"));

            mockMvc.perform(get("/api/posts/99"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/posts/user/{userId}")
    class CreatePostEndpointTests {

        @Test
        @DisplayName("createPost_ShouldReturn200_WhenValidRequest")
        void createPost_ShouldReturn200_WhenValidRequest() throws Exception {
            CreatePostRequest request = CreatePostRequest.builder()
                    .topicId(1L)
                    .title("New Post")
                    .content("New post content")
                    .build();

            PostDTO createdPost = PostDTO.builder()
                    .id(3L)
                    .title("New Post")
                    .content("New post content")
                    .authorId(1L)
                    .topicId(1L)
                    .build();

            when(postService.createPost(eq(1L), any(CreatePostRequest.class)))
                    .thenReturn(createdPost);

            mockMvc.perform(post("/api/posts/user/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(3))
                    .andExpect(jsonPath("$.title").value("New Post"));
        }

        @Test
        @DisplayName("createPost_ShouldReturn404_WhenUserNotFound")
        void createPost_ShouldReturn404_WhenUserNotFound() throws Exception {
            CreatePostRequest request = CreatePostRequest.builder()
                    .topicId(1L)
                    .title("New Post")
                    .content("New post content")
                    .build();

            when(postService.createPost(eq(99L), any(CreatePostRequest.class)))
                    .thenThrow(new ResourceNotFoundException("User not found"));

            mockMvc.perform(post("/api/posts/user/99")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }
}
