package com.openclassrooms.mddapi.controller;

import com.openclassrooms.mddapi.dto.TopicDTO;
import com.openclassrooms.mddapi.exception.GlobalExceptionHandler;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.service.interfaces.ITopicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for TopicController.
 */
@ExtendWith(MockitoExtension.class)
class TopicControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ITopicService topicService;

    @InjectMocks
    private TopicController topicController;

    private TopicDTO topicDTO1;
    private TopicDTO topicDTO2;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(topicController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        topicDTO1 = TopicDTO.builder()
                .id(1L)
                .name("Java")
                .description("Java programming discussions")
                .build();

        topicDTO2 = TopicDTO.builder()
                .id(2L)
                .name("Python")
                .description("Python programming discussions")
                .build();
    }

    @Nested
    @DisplayName("GET /api/topics")
    class GetTopicsEndpointTests {

        @Test
        @DisplayName("getTopics_ShouldReturn200_WhenTopicsExist")
        void getTopics_ShouldReturn200_WhenTopicsExist() throws Exception {
            List<TopicDTO> topics = Arrays.asList(topicDTO1, topicDTO2);
            when(topicService.getTopics()).thenReturn(topics);

            mockMvc.perform(get("/api/topics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].name").value("Java"))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[1].name").value("Python"));
        }

        @Test
        @DisplayName("getTopics_ShouldReturn200WithEmptyArray_WhenNoTopicsExist")
        void getTopics_ShouldReturn200WithEmptyArray_WhenNoTopicsExist() throws Exception {
            when(topicService.getTopics()).thenReturn(Collections.emptyList());

            mockMvc.perform(get("/api/topics"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(0));
        }
    }

    @Nested
    @DisplayName("GET /api/topics/{id}")
    class GetTopicByIdEndpointTests {

        @Test
        @DisplayName("getTopicById_ShouldReturn200_WhenTopicExists")
        void getTopicById_ShouldReturn200_WhenTopicExists() throws Exception {
            when(topicService.getTopicById(1L)).thenReturn(topicDTO1);

            mockMvc.perform(get("/api/topics/1"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Java"))
                    .andExpect(jsonPath("$.description").value("Java programming discussions"));
        }

        @Test
        @DisplayName("getTopicById_ShouldReturn404_WhenTopicNotFound")
        void getTopicById_ShouldReturn404_WhenTopicNotFound() throws Exception {
            when(topicService.getTopicById(99L))
                    .thenThrow(new ResourceNotFoundException("Topic not found"));

            mockMvc.perform(get("/api/topics/99"))
                    .andExpect(status().isNotFound());
        }
    }
}
