package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.dto.TopicDTO;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.mapper.TopicMapper;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.service.implementations.TopicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TopicService.
 */
@ExtendWith(MockitoExtension.class)
class TopicServiceTest {

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private TopicMapper topicMapper;

    @InjectMocks
    private TopicService topicService;

    private Topic topic1;
    private Topic topic2;
    private TopicDTO topicDTO1;
    private TopicDTO topicDTO2;

    @BeforeEach
    void setUp() {
        topic1 = Topic.builder()
                .id(1L)
                .name("Java")
                .description("Java programming discussions")
                .build();

        topic2 = Topic.builder()
                .id(2L)
                .name("Python")
                .description("Python programming discussions")
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
    @DisplayName("GetTopics Tests")
    class GetTopicsTests {

        @Test
        @DisplayName("getTopics_ShouldReturnListOfTopics_WhenTopicsExist")
        void getTopics_ShouldReturnListOfTopics_WhenTopicsExist() {
            List<Topic> topics = Arrays.asList(topic1, topic2);
            List<TopicDTO> topicDTOs = Arrays.asList(topicDTO1, topicDTO2);

            when(topicRepository.findAll()).thenReturn(topics);
            when(topicMapper.toDTOList(topics)).thenReturn(topicDTOs);

            List<TopicDTO> result = topicService.getTopics();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("Java");
            assertThat(result.get(1).getName()).isEqualTo("Python");

            verify(topicRepository).findAll();
            verify(topicMapper).toDTOList(topics);
        }

        @Test
        @DisplayName("getTopics_ShouldReturnEmptyList_WhenNoTopicsExist")
        void getTopics_ShouldReturnEmptyList_WhenNoTopicsExist() {
            when(topicRepository.findAll()).thenReturn(Collections.emptyList());
            when(topicMapper.toDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

            List<TopicDTO> result = topicService.getTopics();

            assertThat(result).isEmpty();

            verify(topicRepository).findAll();
        }
    }

    @Nested
    @DisplayName("GetTopicById Tests")
    class GetTopicByIdTests {

        @Test
        @DisplayName("getTopicById_ShouldReturnTopicDTO_WhenTopicExists")
        void getTopicById_ShouldReturnTopicDTO_WhenTopicExists() {
            when(topicRepository.findById(1L)).thenReturn(Optional.of(topic1));
            when(topicMapper.toDTO(topic1)).thenReturn(topicDTO1);

            TopicDTO result = topicService.getTopicById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("Java");
            assertThat(result.getDescription()).isEqualTo("Java programming discussions");

            verify(topicRepository).findById(1L);
            verify(topicMapper).toDTO(topic1);
        }

        @Test
        @DisplayName("getTopicById_ShouldThrowResourceNotFoundException_WhenTopicNotFound")
        void getTopicById_ShouldThrowResourceNotFoundException_WhenTopicNotFound() {
            when(topicRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> topicService.getTopicById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Topic not found with id: 99");

            verify(topicRepository).findById(99L);
            verify(topicMapper, never()).toDTO(any(Topic.class));
        }
    }
}
