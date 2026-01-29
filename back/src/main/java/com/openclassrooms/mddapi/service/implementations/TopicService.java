package com.openclassrooms.mddapi.service.implementations;

import java.util.List;

import com.openclassrooms.mddapi.service.interfaces.ITopicService;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.dto.TopicDTO;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.mapper.TopicMapper;
import com.openclassrooms.mddapi.repository.TopicRepository;

import lombok.RequiredArgsConstructor;

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
