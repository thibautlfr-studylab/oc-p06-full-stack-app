package com.openclassrooms.mddapi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.PostDTO;
import com.openclassrooms.mddapi.dto.request.CreatePostRequest;
import com.openclassrooms.mddapi.service.interfaces.IPostService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for Post operations.
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

	private final IPostService postService;

	/**
	 * Get feed posts for a user based on subscribed topics.
	 *
	 * @param userId the user ID
	 * @param ascending optional sort order (default: false = newest first)
	 * @return list of post DTOs from subscribed topics
	 */
	@GetMapping("/feed/{userId}")
	public ResponseEntity<List<PostDTO>> getFeed(
			@PathVariable Long userId,
			@RequestParam(defaultValue = "false") boolean ascending) {
		return ResponseEntity.ok(postService.getFeedForUser(userId, ascending));
	}

	/**
	 * Get a post by its ID.
	 *
	 * @param id the post ID
	 * @return the post DTO
	 */
	@GetMapping("/{id}")
	public ResponseEntity<PostDTO> getPostById(@PathVariable Long id) {
		return ResponseEntity.ok(postService.getPostById(id));
	}

	/**
	 * Create a new post.
	 *
	 * @param userId the author's user ID
	 * @param request the creation request containing topicId, title, and content
	 * @return the created post DTO
	 */
	@PostMapping("/user/{userId}")
	public ResponseEntity<PostDTO> createPost(
			@PathVariable Long userId,
			@Valid @RequestBody CreatePostRequest request) {
		return ResponseEntity.ok(postService.createPost(userId, request));
	}
}
