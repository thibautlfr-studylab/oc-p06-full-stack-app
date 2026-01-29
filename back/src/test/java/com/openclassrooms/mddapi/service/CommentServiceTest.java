package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.CommentDTO;
import com.openclassrooms.mddapi.dto.request.CreateCommentRequest;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.mapper.CommentMapper;
import com.openclassrooms.mddapi.model.Comment;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.service.implementations.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CommentService.
 */
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    private User testUser;
    private Topic testTopic;
    private Post testPost;
    private Comment testComment;
    private CommentDTO testCommentDTO;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .build();

        testTopic = Topic.builder()
                .id(1L)
                .name("Java")
                .description("Java discussions")
                .build();

        testPost = Post.builder()
                .id(1L)
                .title("Test Post")
                .content("This is test content")
                .author(testUser)
                .topic(testTopic)
                .createdAt(LocalDateTime.now())
                .build();

        testComment = Comment.builder()
                .id(1L)
                .content("This is a comment")
                .author(testUser)
                .post(testPost)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testCommentDTO = CommentDTO.builder()
                .id(1L)
                .content("This is a comment")
                .authorId(1L)
                .authorUsername("testuser")
                .postId(1L)
                .createdAt(testComment.getCreatedAt())
                .updatedAt(testComment.getUpdatedAt())
                .build();
    }

    @Nested
    @DisplayName("GetCommentsForPost Tests")
    class GetCommentsForPostTests {

        @Test
        @DisplayName("getCommentsForPost_ShouldReturnCommentDTOs_WhenCommentsExist")
        void getCommentsForPost_ShouldReturnCommentDTOs_WhenCommentsExist() {
            Comment comment2 = Comment.builder()
                    .id(2L)
                    .content("Another comment")
                    .author(testUser)
                    .post(testPost)
                    .createdAt(LocalDateTime.now())
                    .build();

            CommentDTO commentDTO2 = CommentDTO.builder()
                    .id(2L)
                    .content("Another comment")
                    .authorId(1L)
                    .postId(1L)
                    .build();

            List<Comment> comments = Arrays.asList(testComment, comment2);
            List<CommentDTO> commentDTOs = Arrays.asList(testCommentDTO, commentDTO2);

            when(commentRepository.findByPostIdWithAuthor(1L)).thenReturn(comments);
            when(commentMapper.toDTOList(comments)).thenReturn(commentDTOs);

            List<CommentDTO> result = commentService.getCommentsForPost(1L);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getContent()).isEqualTo("This is a comment");
            assertThat(result.get(1).getContent()).isEqualTo("Another comment");

            verify(commentRepository).findByPostIdWithAuthor(1L);
            verify(commentMapper).toDTOList(comments);
        }

        @Test
        @DisplayName("getCommentsForPost_ShouldReturnEmptyList_WhenNoCommentsExist")
        void getCommentsForPost_ShouldReturnEmptyList_WhenNoCommentsExist() {
            when(commentRepository.findByPostIdWithAuthor(1L)).thenReturn(Collections.emptyList());
            when(commentMapper.toDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

            List<CommentDTO> result = commentService.getCommentsForPost(1L);

            assertThat(result).isEmpty();

            verify(commentRepository).findByPostIdWithAuthor(1L);
        }
    }

    @Nested
    @DisplayName("AddComment Tests")
    class AddCommentTests {

        @Test
        @DisplayName("addComment_ShouldReturnCreatedCommentDTO_WhenValidRequest")
        void addComment_ShouldReturnCreatedCommentDTO_WhenValidRequest() {
            CreateCommentRequest request = CreateCommentRequest.builder()
                    .content("New comment")
                    .build();

            Comment newComment = Comment.builder()
                    .id(2L)
                    .content("New comment")
                    .author(testUser)
                    .post(testPost)
                    .createdAt(LocalDateTime.now())
                    .build();

            CommentDTO newCommentDTO = CommentDTO.builder()
                    .id(2L)
                    .content("New comment")
                    .authorId(1L)
                    .authorUsername("testuser")
                    .postId(1L)
                    .build();

            when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(commentRepository.save(any(Comment.class))).thenReturn(newComment);
            when(commentMapper.toDTO(newComment)).thenReturn(newCommentDTO);

            CommentDTO result = commentService.addComment(1L, 1L, request);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEqualTo("New comment");
            assertThat(result.getAuthorId()).isEqualTo(1L);
            assertThat(result.getPostId()).isEqualTo(1L);

            verify(postRepository).findById(1L);
            verify(userRepository).findById(1L);
            verify(commentRepository).save(any(Comment.class));
        }

        @Test
        @DisplayName("addComment_ShouldThrowResourceNotFoundException_WhenPostNotFound")
        void addComment_ShouldThrowResourceNotFoundException_WhenPostNotFound() {
            CreateCommentRequest request = CreateCommentRequest.builder()
                    .content("New comment")
                    .build();

            when(postRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> commentService.addComment(99L, 1L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Post not found with id: 99");

            verify(userRepository, never()).findById(anyLong());
            verify(commentRepository, never()).save(any(Comment.class));
        }

        @Test
        @DisplayName("addComment_ShouldThrowResourceNotFoundException_WhenUserNotFound")
        void addComment_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
            CreateCommentRequest request = CreateCommentRequest.builder()
                    .content("New comment")
                    .build();

            when(postRepository.findById(1L)).thenReturn(Optional.of(testPost));
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> commentService.addComment(1L, 99L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("User not found with id: 99");

            verify(commentRepository, never()).save(any(Comment.class));
        }
    }
}
