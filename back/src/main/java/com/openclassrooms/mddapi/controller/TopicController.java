package com.openclassrooms.mddapi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.TopicDTO;
import com.openclassrooms.mddapi.service.interfaces.ITopicService;

import lombok.RequiredArgsConstructor;

/**
 * REST controller for Topic operations.
 */
@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

	private final ITopicService topicService;

	/**
	 * Retrieves all available topics.
	 *
	 * @return list of all topics
	 */
	@GetMapping
	public ResponseEntity<List<TopicDTO>> getTopics() {
		return ResponseEntity.ok(topicService.getTopics());
	}

	/**
	 * Retrieves a topic by its ID.
	 *
	 * @param id the topic ID
	 * @return the topic if found, or 404 if not
	 */
	@GetMapping("/{id}")
	public ResponseEntity<TopicDTO> getTopicById(@PathVariable Long id) {
		TopicDTO topic = topicService.getTopicById(id);
		if (topic == null) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(topic);
	}
}
