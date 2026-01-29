package com.openclassrooms.mddapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.mddapi.dto.SubscriptionDTO;
import com.openclassrooms.mddapi.dto.request.SubscribeRequest;
import com.openclassrooms.mddapi.exception.AlreadySubscribedException;
import com.openclassrooms.mddapi.exception.GlobalExceptionHandler;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.service.interfaces.ISubscriptionService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for SubscriptionController.
 */
@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ISubscriptionService subscriptionService;

    @InjectMocks
    private SubscriptionController subscriptionController;

    private ObjectMapper objectMapper;
    private SubscriptionDTO subscriptionDTO1;
    private SubscriptionDTO subscriptionDTO2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(subscriptionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        subscriptionDTO1 = SubscriptionDTO.builder()
                .id(1L)
                .userId(1L)
                .topicId(1L)
                .topicName("Java")
                .topicDescription("Java discussions")
                .subscribedAt(LocalDateTime.now())
                .build();

        subscriptionDTO2 = SubscriptionDTO.builder()
                .id(2L)
                .userId(1L)
                .topicId(2L)
                .topicName("Python")
                .topicDescription("Python discussions")
                .subscribedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("GET /api/subscriptions/user/{userId}")
    class GetUserSubscriptionsEndpointTests {

        @Test
        @DisplayName("getUserSubscriptions_ShouldReturn200_WhenSubscriptionsExist")
        void getUserSubscriptions_ShouldReturn200_WhenSubscriptionsExist() throws Exception {
            List<SubscriptionDTO> subscriptions = Arrays.asList(subscriptionDTO1, subscriptionDTO2);
            when(subscriptionService.getSubscriptionsByUserId(1L)).thenReturn(subscriptions);

            mockMvc.perform(get("/api/subscriptions/user/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].topicName").value("Java"))
                    .andExpect(jsonPath("$[1].topicName").value("Python"));
        }

        @Test
        @DisplayName("getUserSubscriptions_ShouldReturn200WithEmptyArray_WhenNoSubscriptions")
        void getUserSubscriptions_ShouldReturn200WithEmptyArray_WhenNoSubscriptions() throws Exception {
            when(subscriptionService.getSubscriptionsByUserId(1L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/subscriptions/user/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/subscriptions/user/{userId}/topic-ids")
    class GetSubscribedTopicIdsEndpointTests {

        @Test
        @DisplayName("getSubscribedTopicIds_ShouldReturn200_WhenTopicIdsExist")
        void getSubscribedTopicIds_ShouldReturn200_WhenTopicIdsExist() throws Exception {
            List<Long> topicIds = Arrays.asList(1L, 2L);
            when(subscriptionService.getSubscribedTopicIds(1L)).thenReturn(topicIds);

            mockMvc.perform(get("/api/subscriptions/user/1/topic-ids"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0]").value(1))
                    .andExpect(jsonPath("$[1]").value(2));
        }

        @Test
        @DisplayName("getSubscribedTopicIds_ShouldReturn200WithEmptyArray_WhenNoSubscriptions")
        void getSubscribedTopicIds_ShouldReturn200WithEmptyArray_WhenNoSubscriptions() throws Exception {
            when(subscriptionService.getSubscribedTopicIds(1L)).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/subscriptions/user/1/topic-ids"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("POST /api/subscriptions")
    class SubscribeEndpointTests {

        @Test
        @DisplayName("subscribe_ShouldReturn200_WhenValidRequest")
        void subscribe_ShouldReturn200_WhenValidRequest() throws Exception {
            SubscribeRequest request = SubscribeRequest.builder()
                    .userId(1L)
                    .topicId(1L)
                    .build();

            when(subscriptionService.subscribe(1L, 1L)).thenReturn(subscriptionDTO1);

            mockMvc.perform(post("/api/subscriptions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.userId").value(1))
                    .andExpect(jsonPath("$.topicId").value(1))
                    .andExpect(jsonPath("$.topicName").value("Java"));
        }

        @Test
        @DisplayName("subscribe_ShouldReturn409_WhenAlreadySubscribed")
        void subscribe_ShouldReturn409_WhenAlreadySubscribed() throws Exception {
            SubscribeRequest request = SubscribeRequest.builder()
                    .userId(1L)
                    .topicId(1L)
                    .build();

            when(subscriptionService.subscribe(1L, 1L))
                    .thenThrow(new AlreadySubscribedException("Already subscribed"));

            mockMvc.perform(post("/api/subscriptions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("DELETE /api/subscriptions/user/{userId}/topic/{topicId}")
    class UnsubscribeEndpointTests {

        @Test
        @DisplayName("unsubscribe_ShouldReturn200_WhenSubscriptionExists")
        void unsubscribe_ShouldReturn200_WhenSubscriptionExists() throws Exception {
            doNothing().when(subscriptionService).unsubscribe(1L, 1L);

            mockMvc.perform(delete("/api/subscriptions/user/1/topic/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("Successfully unsubscribed"));

            verify(subscriptionService).unsubscribe(1L, 1L);
        }

        @Test
        @DisplayName("unsubscribe_ShouldReturn404_WhenSubscriptionNotFound")
        void unsubscribe_ShouldReturn404_WhenSubscriptionNotFound() throws Exception {
            doThrow(new ResourceNotFoundException("Subscription not found"))
                    .when(subscriptionService).unsubscribe(1L, 99L);

            mockMvc.perform(delete("/api/subscriptions/user/1/topic/99"))
                    .andExpect(status().isNotFound());
        }
    }
}
