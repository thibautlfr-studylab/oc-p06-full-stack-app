package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.PostDTO;
import com.openclassrooms.mddapi.dto.request.CreatePostRequest;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.mapper.PostMapper;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.Subscription;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.service.implementations.PostService;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PostService.
 */
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostService postService;

    private User testUser;
    private Topic testTopic;
    private Post testPost;
    private PostDTO testPostDTO;
    private Subscription testSubscription;

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
                .updatedAt(LocalDateTime.now())
                .build();

        testPostDTO = PostDTO.builder()
                .id(1L)
                .title("Test Post")
                .content("This is test content")
                .authorId(1L)
                .authorUsername("testuser")
                .topicId(1L)
                .topicName("Java")
                .createdAt(testPost.getCreatedAt())
                .updatedAt(testPost.getUpdatedAt())
                .build();

        testSubscription = Subscription.builder()
                .id(1L)
                .user(testUser)
                .topic(testTopic)
                .subscribedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("GetFeedForUser Tests")
    class GetFeedForUserTests {

        @Test
        @DisplayName("getFeedForUser_ShouldReturnPostsDescending_WhenAscendingIsFalse")
        void getFeedForUser_ShouldReturnPostsDescending_WhenAscendingIsFalse() {
            List<Subscription> subscriptions = Collections.singletonList(testSubscription);
            List<Post> posts = Collections.singletonList(testPost);
            List<PostDTO> postDTOs = Collections.singletonList(testPostDTO);

            when(subscriptionRepository.findByUserId(1L)).thenReturn(subscriptions);
            when(postRepository.findByTopicIdInOrderByCreatedAtDesc(anyList())).thenReturn(posts);
            when(postMapper.toDTOList(posts)).thenReturn(postDTOs);

            List<PostDTO> result = postService.getFeedForUser(1L, false);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getTitle()).isEqualTo("Test Post");

            verify(postRepository).findByTopicIdInOrderByCreatedAtDesc(anyList());
            verify(postRepository, never()).findByTopicIdInOrderByCreatedAtAsc(anyList());
        }

        @Test
        @DisplayName("getFeedForUser_ShouldReturnPostsAscending_WhenAscendingIsTrue")
        void getFeedForUser_ShouldReturnPostsAscending_WhenAscendingIsTrue() {
            List<Subscription> subscriptions = Collections.singletonList(testSubscription);
            List<Post> posts = Collections.singletonList(testPost);
            List<PostDTO> postDTOs = Collections.singletonList(testPostDTO);

            when(subscriptionRepository.findByUserId(1L)).thenReturn(subscriptions);
            when(postRepository.findByTopicIdInOrderByCreatedAtAsc(anyList())).thenReturn(posts);
            when(postMapper.toDTOList(posts)).thenReturn(postDTOs);

            List<PostDTO> result = postService.getFeedForUser(1L, true);

            assertThat(result).hasSize(1);

            verify(postRepository).findByTopicIdInOrderByCreatedAtAsc(anyList());
            verify(postRepository, never()).findByTopicIdInOrderByCreatedAtDesc(anyList());
        }

        @Test
        @DisplayName("getFeedForUser_ShouldReturnEmptyList_WhenNoSubscriptions")
        void getFeedForUser_ShouldReturnEmptyList_WhenNoSubscriptions() {
            when(subscriptionRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

            List<PostDTO> result = postService.getFeedForUser(1L, false);

            assertThat(result).isEmpty();

            verify(postRepository, never()).findByTopicIdInOrderByCreatedAtDesc(anyList());
            verify(postRepository, never()).findByTopicIdInOrderByCreatedAtAsc(anyList());
        }

        @Test
        @DisplayName("getFeedForUser_ShouldReturnMultiplePosts_WhenMultipleSubscriptions")
        void getFeedForUser_ShouldReturnMultiplePosts_WhenMultipleSubscriptions() {
            Topic topic2 = Topic.builder().id(2L).name("Python").build();
            Subscription subscription2 = Subscription.builder()
                    .id(2L)
                    .user(testUser)
                    .topic(topic2)
                    .build();

            Post post2 = Post.builder()
                    .id(2L)
                    .title("Python Post")
                    .content("Python content")
                    .author(testUser)
                    .topic(topic2)
                    .build();

            PostDTO postDTO2 = PostDTO.builder()
                    .id(2L)
                    .title("Python Post")
                    .topicName("Python")
                    .build();

            List<Subscription> subscriptions = Arrays.asList(testSubscription, subscription2);
            List<Post> posts = Arrays.asList(testPost, post2);
            List<PostDTO> postDTOs = Arrays.asList(testPostDTO, postDTO2);

            when(subscriptionRepository.findByUserId(1L)).thenReturn(subscriptions);
            when(postRepository.findByTopicIdInOrderByCreatedAtDesc(anyList())).thenReturn(posts);
            when(postMapper.toDTOList(posts)).thenReturn(postDTOs);

            List<PostDTO> result = postService.getFeedForUser(1L, false);

            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("GetPostById Tests")
    class GetPostByIdTests {

        @Test
        @DisplayName("getPostById_ShouldReturnPostDTO_WhenPostExists")
        void getPostById_ShouldReturnPostDTO_WhenPostExists() {
            when(postRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(testPost));
            when(postMapper.toDTO(testPost)).thenReturn(testPostDTO);

            PostDTO result = postService.getPostById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTitle()).isEqualTo("Test Post");
            assertThat(result.getAuthorUsername()).isEqualTo("testuser");
            assertThat(result.getTopicName()).isEqualTo("Java");

            verify(postRepository).findByIdWithDetails(1L);
        }

        @Test
        @DisplayName("getPostById_ShouldThrowResourceNotFoundException_WhenPostNotFound")
        void getPostById_ShouldThrowResourceNotFoundException_WhenPostNotFound() {
            when(postRepository.findByIdWithDetails(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> postService.getPostById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Post not found with id: 99");

            verify(postMapper, never()).toDTO(any(Post.class));
        }
    }

    @Nested
    @DisplayName("CreatePost Tests")
    class CreatePostTests {

        @Test
        @DisplayName("createPost_ShouldReturnCreatedPostDTO_WhenValidRequest")
        void createPost_ShouldReturnCreatedPostDTO_WhenValidRequest() {
            CreatePostRequest request = CreatePostRequest.builder()
                    .topicId(1L)
                    .title("New Post")
                    .content("New content")
                    .build();

            Post newPost = Post.builder()
                    .id(2L)
                    .title("New Post")
                    .content("New content")
                    .author(testUser)
                    .topic(testTopic)
                    .build();

            PostDTO newPostDTO = PostDTO.builder()
                    .id(2L)
                    .title("New Post")
                    .content("New content")
                    .authorId(1L)
                    .topicId(1L)
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
            when(postRepository.save(any(Post.class))).thenReturn(newPost);
            when(postMapper.toDTO(newPost)).thenReturn(newPostDTO);

            PostDTO result = postService.createPost(1L, request);

            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("New Post");
            assertThat(result.getContent()).isEqualTo("New content");

            verify(userRepository).findById(1L);
            verify(topicRepository).findById(1L);
            verify(postRepository).save(any(Post.class));
        }

        @Test
        @DisplayName("createPost_ShouldThrowResourceNotFoundException_WhenUserNotFound")
        void createPost_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
            CreatePostRequest request = CreatePostRequest.builder()
                    .topicId(1L)
                    .title("New Post")
                    .content("New content")
                    .build();

            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> postService.createPost(99L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("User not found with id: 99");

            verify(topicRepository, never()).findById(anyLong());
            verify(postRepository, never()).save(any(Post.class));
        }

        @Test
        @DisplayName("createPost_ShouldThrowResourceNotFoundException_WhenTopicNotFound")
        void createPost_ShouldThrowResourceNotFoundException_WhenTopicNotFound() {
            CreatePostRequest request = CreatePostRequest.builder()
                    .topicId(99L)
                    .title("New Post")
                    .content("New content")
                    .build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(topicRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> postService.createPost(1L, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Topic not found with id: 99");

            verify(postRepository, never()).save(any(Post.class));
        }
    }
}
