package com.openclassrooms.mddapi.service.implementations;

import com.openclassrooms.mddapi.dto.TopicDTO;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.mapper.TopicMapper;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.service.interfaces.ITopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service implementation for Topic operations.
 */
@Service
@RequiredArgsConstructor
public class TopicService implements ITopicService {

  private final TopicRepository topicRepository;
  private final TopicMapper topicMapper;

  @Override
  public List<TopicDTO> getTopics() {
    return topicMapper.toDTOList(topicRepository.findAll());
  }

  @Override
  public TopicDTO getTopicById(Long id) {
    return topicRepository.findById(id)
        .map(topicMapper::toDTO)
        .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + id));
  }
}
