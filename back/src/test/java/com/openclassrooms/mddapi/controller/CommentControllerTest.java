package com.openclassrooms.mddapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mddapi.dto.CommentDTO;
import com.openclassrooms.mddapi.dto.request.CreateCommentRequest;
import com.openclassrooms.mddapi.exception.GlobalExceptionHandler;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.service.interfaces.ICommentService;
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
 * Unit tests for CommentController.
 */
@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ICommentService commentService;

    @InjectMocks
    private CommentController commentController;

    private ObjectMapper objectMapper;
    private CommentDTO commentDTO1;
    private CommentDTO commentDTO2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        commentDTO1 = CommentDTO.builder()
                .id(1L)
                .content("First comment")
                .authorId(1L)
                .authorUsername("testuser")
                .postId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        commentDTO2 = CommentDTO.builder()
                .id(2L)
                .content("Second comment")
                .authorId(2L)
                .authorUsername("anotheruser")
                .postId(1L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("GET /api/comments/post/{postId}")
    class GetCommentsForPostEndpointTests {

        @Test
        @DisplayName("getCommentsForPost_ShouldReturn200_WhenCommentsExist")
        void getCommentsForPost_ShouldReturn200_WhenCommentsExist() throws Exception {
            List<CommentDTO> comments = Arrays.asList(commentDTO1, commentDTO2);
            when(commentService.getCommentsForPost(1L)).thenReturn(comments);

            mockMvc.perform(get("/api/comments/post/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].content").value("First comment"))
                    .andExpect(jsonPath("$[0].authorUsername").value("testuser"))
                    .andExpect(jsonPath("$[1].content").value("Second comment"));
        }

        @Test
        @DisplayName("getCommentsForPost_ShouldReturn200WithEmptyArray_WhenNoComments")
        void getCommentsForPost_ShouldReturn200WithEmptyArray_WhenNoComments() throws Exception {
            when(commentService.getCommentsForPost(1L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/comments/post/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("POST /api/comments/post/{postId}/user/{userId}")
    class AddCommentEndpointTests {

        @Test
        @DisplayName("addComment_ShouldReturn200_WhenValidRequest")
        void addComment_ShouldReturn200_WhenValidRequest() throws Exception {
            CreateCommentRequest request = CreateCommentRequest.builder()
                    .content("New comment")
                    .build();

            CommentDTO createdComment = CommentDTO.builder()
                    .id(3L)
                    .content("New comment")
                    .authorId(1L)
                    .authorUsername("testuser")
                    .postId(1L)
                    .build();

            when(commentService.addComment(eq(1L), eq(1L), any(CreateCommentRequest.class)))
                    .thenReturn(createdComment);

            mockMvc.perform(post("/api/comments/post/1/user/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(3))
                    .andExpect(jsonPath("$.content").value("New comment"))
                    .andExpect(jsonPath("$.authorUsername").value("testuser"));
        }

        @Test
        @DisplayName("addComment_ShouldReturn404_WhenPostNotFound")
        void addComment_ShouldReturn404_WhenPostNotFound() throws Exception {
            CreateCommentRequest request = CreateCommentRequest.builder()
                    .content("New comment")
                    .build();

            when(commentService.addComment(eq(99L), eq(1L), any(CreateCommentRequest.class)))
                    .thenThrow(new ResourceNotFoundException("Post not found"));

            mockMvc.perform(post("/api/comments/post/99/user/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound());
        }
    }
}
