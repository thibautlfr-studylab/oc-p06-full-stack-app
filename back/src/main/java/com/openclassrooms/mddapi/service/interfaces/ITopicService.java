package com.openclassrooms.mddapi.service.interfaces;

import java.util.List;

import com.openclassrooms.mddapi.dto.TopicDTO;

/**
 * Service interface for Topic operations.
 */
public interface ITopicService {

	/**
	 * Retrieves all available topics.
	 *
	 * @return list of all topics as DTOs
	 */
	List<TopicDTO> getTopics();

	/**
	 * Retrieves a topic by its ID.
	 *
	 * @param id the topic ID
	 * @return the topic DTO, or null if not found
	 */
	TopicDTO getTopicById(Long id);
}
