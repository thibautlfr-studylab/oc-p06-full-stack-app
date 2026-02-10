package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.SubscriptionDTO;
import com.openclassrooms.mddapi.exception.AlreadySubscribedException;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.mapper.SubscriptionMapper;
import com.openclassrooms.mddapi.model.Subscription;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.service.implementations.SubscriptionService;
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
 * Unit tests for SubscriptionService.
 */
@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private SubscriptionMapper subscriptionMapper;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private User testUser;
    private Topic testTopic;
    private Topic testTopic2;
    private Subscription testSubscription;
    private SubscriptionDTO testSubscriptionDTO;

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

        testTopic2 = Topic.builder()
                .id(2L)
                .name("Python")
                .description("Python discussions")
                .build();

        testSubscription = Subscription.builder()
                .id(1L)
                .user(testUser)
                .topic(testTopic)
                .subscribedAt(LocalDateTime.now())
                .build();

        testSubscriptionDTO = SubscriptionDTO.builder()
                .id(1L)
                .userId(1L)
                .topicId(1L)
                .topicName("Java")
                .topicDescription("Java discussions")
                .subscribedAt(testSubscription.getSubscribedAt())
                .build();
    }

    @Nested
    @DisplayName("GetSubscriptionsByUserId Tests")
    class GetSubscriptionsByUserIdTests {

        @Test
        @DisplayName("getSubscriptionsByUserId_ShouldReturnSubscriptionDTOs_WhenSubscriptionsExist")
        void getSubscriptionsByUserId_ShouldReturnSubscriptionDTOs_WhenSubscriptionsExist() {
            Subscription subscription2 = Subscription.builder()
                    .id(2L)
                    .user(testUser)
                    .topic(testTopic2)
                    .subscribedAt(LocalDateTime.now())
                    .build();

            SubscriptionDTO subscriptionDTO2 = SubscriptionDTO.builder()
                    .id(2L)
                    .userId(1L)
                    .topicId(2L)
                    .topicName("Python")
                    .build();

            List<Subscription> subscriptions = Arrays.asList(testSubscription, subscription2);
            List<SubscriptionDTO> subscriptionDTOs = Arrays.asList(testSubscriptionDTO, subscriptionDTO2);

            when(subscriptionRepository.findByUserId(1L)).thenReturn(subscriptions);
            when(subscriptionMapper.toDTOList(subscriptions)).thenReturn(subscriptionDTOs);

            List<SubscriptionDTO> result = subscriptionService.getSubscriptionsByUserId(1L);

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getTopicName()).isEqualTo("Java");
            assertThat(result.get(1).getTopicName()).isEqualTo("Python");

            verify(subscriptionRepository).findByUserId(1L);
            verify(subscriptionMapper).toDTOList(subscriptions);
        }

        @Test
        @DisplayName("getSubscriptionsByUserId_ShouldReturnEmptyList_WhenNoSubscriptions")
        void getSubscriptionsByUserId_ShouldReturnEmptyList_WhenNoSubscriptions() {
            when(subscriptionRepository.findByUserId(1L)).thenReturn(Collections.emptyList());
            when(subscriptionMapper.toDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

            List<SubscriptionDTO> result = subscriptionService.getSubscriptionsByUserId(1L);

            assertThat(result).isEmpty();

            verify(subscriptionRepository).findByUserId(1L);
        }
    }

    @Nested
    @DisplayName("GetSubscribedTopicIds Tests")
    class GetSubscribedTopicIdsTests {

        @Test
        @DisplayName("getSubscribedTopicIds_ShouldReturnTopicIds_WhenSubscriptionsExist")
        void getSubscribedTopicIds_ShouldReturnTopicIds_WhenSubscriptionsExist() {
            Subscription subscription2 = Subscription.builder()
                    .id(2L)
                    .user(testUser)
                    .topic(testTopic2)
                    .build();

            List<Subscription> subscriptions = Arrays.asList(testSubscription, subscription2);

            when(subscriptionRepository.findByUserId(1L)).thenReturn(subscriptions);

            List<Long> result = subscriptionService.getSubscribedTopicIds(1L);

            assertThat(result).containsExactly(1L, 2L);

            verify(subscriptionRepository).findByUserId(1L);
        }

        @Test
        @DisplayName("getSubscribedTopicIds_ShouldReturnEmptyList_WhenNoSubscriptions")
        void getSubscribedTopicIds_ShouldReturnEmptyList_WhenNoSubscriptions() {
            when(subscriptionRepository.findByUserId(1L)).thenReturn(Collections.emptyList());

            List<Long> result = subscriptionService.getSubscribedTopicIds(1L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("Subscribe Tests")
    class SubscribeTests {

        @Test
        @DisplayName("subscribe_ShouldReturnSubscriptionDTO_WhenValidRequest")
        void subscribe_ShouldReturnSubscriptionDTO_WhenValidRequest() {
            when(subscriptionRepository.existsByUserIdAndTopicId(1L, 1L)).thenReturn(false);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
            when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscription);
            when(subscriptionMapper.toDTO(testSubscription)).thenReturn(testSubscriptionDTO);

            SubscriptionDTO result = subscriptionService.subscribe(1L, 1L);

            assertThat(result).isNotNull();
            assertThat(result.getUserId()).isEqualTo(1L);
            assertThat(result.getTopicId()).isEqualTo(1L);
            assertThat(result.getTopicName()).isEqualTo("Java");

            verify(subscriptionRepository).existsByUserIdAndTopicId(1L, 1L);
            verify(userRepository).findById(1L);
            verify(topicRepository).findById(1L);
            verify(subscriptionRepository).save(any(Subscription.class));
        }

        @Test
        @DisplayName("subscribe_ShouldThrowAlreadySubscribedException_WhenAlreadySubscribed")
        void subscribe_ShouldThrowAlreadySubscribedException_WhenAlreadySubscribed() {
            when(subscriptionRepository.existsByUserIdAndTopicId(1L, 1L)).thenReturn(true);

            assertThatThrownBy(() -> subscriptionService.subscribe(1L, 1L))
                    .isInstanceOf(AlreadySubscribedException.class)
                    .hasMessage("User is already subscribed to this topic");

            verify(userRepository, never()).findById(anyLong());
            verify(topicRepository, never()).findById(anyLong());
            verify(subscriptionRepository, never()).save(any(Subscription.class));
        }

        @Test
        @DisplayName("subscribe_ShouldThrowResourceNotFoundException_WhenUserNotFound")
        void subscribe_ShouldThrowResourceNotFoundException_WhenUserNotFound() {
            when(subscriptionRepository.existsByUserIdAndTopicId(99L, 1L)).thenReturn(false);
            when(userRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> subscriptionService.subscribe(99L, 1L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("User not found with id: 99");

            verify(topicRepository, never()).findById(anyLong());
            verify(subscriptionRepository, never()).save(any(Subscription.class));
        }

        @Test
        @DisplayName("subscribe_ShouldThrowResourceNotFoundException_WhenTopicNotFound")
        void subscribe_ShouldThrowResourceNotFoundException_WhenTopicNotFound() {
            when(subscriptionRepository.existsByUserIdAndTopicId(1L, 99L)).thenReturn(false);
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            when(topicRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> subscriptionService.subscribe(1L, 99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Topic not found with id: 99");

            verify(subscriptionRepository, never()).save(any(Subscription.class));
        }
    }

    @Nested
    @DisplayName("Unsubscribe Tests")
    class UnsubscribeTests {

        @Test
        @DisplayName("unsubscribe_ShouldDeleteSubscription_WhenSubscriptionExists")
        void unsubscribe_ShouldDeleteSubscription_WhenSubscriptionExists() {
            when(subscriptionRepository.existsByUserIdAndTopicId(1L, 1L)).thenReturn(true);
            doNothing().when(subscriptionRepository).deleteByUserIdAndTopicId(1L, 1L);

            subscriptionService.unsubscribe(1L, 1L);

            verify(subscriptionRepository).existsByUserIdAndTopicId(1L, 1L);
            verify(subscriptionRepository).deleteByUserIdAndTopicId(1L, 1L);
        }

        @Test
        @DisplayName("unsubscribe_ShouldThrowResourceNotFoundException_WhenSubscriptionNotFound")
        void unsubscribe_ShouldThrowResourceNotFoundException_WhenSubscriptionNotFound() {
            when(subscriptionRepository.existsByUserIdAndTopicId(1L, 99L)).thenReturn(false);

            assertThatThrownBy(() -> subscriptionService.unsubscribe(1L, 99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Subscription not found for user 1 and topic 99");

            verify(subscriptionRepository, never()).deleteByUserIdAndTopicId(anyLong(), anyLong());
        }
    }

    @Nested
    @DisplayName("IsSubscribed Tests")
    class IsSubscribedTests {

        @Test
        @DisplayName("isSubscribed_ShouldReturnTrue_WhenUserIsSubscribed")
        void isSubscribed_ShouldReturnTrue_WhenUserIsSubscribed() {
            when(subscriptionRepository.existsByUserIdAndTopicId(1L, 1L)).thenReturn(true);

            boolean result = subscriptionService.isSubscribed(1L, 1L);

            assertThat(result).isTrue();

            verify(subscriptionRepository).existsByUserIdAndTopicId(1L, 1L);
        }

        @Test
        @DisplayName("isSubscribed_ShouldReturnFalse_WhenUserIsNotSubscribed")
        void isSubscribed_ShouldReturnFalse_WhenUserIsNotSubscribed() {
            when(subscriptionRepository.existsByUserIdAndTopicId(1L, 99L)).thenReturn(false);

            boolean result = subscriptionService.isSubscribed(1L, 99L);

            assertThat(result).isFalse();

            verify(subscriptionRepository).existsByUserIdAndTopicId(1L, 99L);
        }
    }
}
