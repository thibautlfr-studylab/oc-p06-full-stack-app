package com.openclassrooms.mddapi.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.mddapi.dto.CommentDTO;
import com.openclassrooms.mddapi.dto.request.CreateCommentRequest;
import com.openclassrooms.mddapi.service.interfaces.ICommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST controller for Comment operations.
 */
@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

	private final ICommentService commentService;

	/**
	 * Get all comments for a specific post.
	 *
	 * @param postId the post ID
	 * @return list of comment DTOs ordered by creation date ascending
	 */
	@GetMapping("/post/{postId}")
	public ResponseEntity<List<CommentDTO>> getCommentsForPost(@PathVariable Long postId) {
		return ResponseEntity.ok(commentService.getCommentsForPost(postId));
	}

	/**
	 * Add a comment to a post.
	 *
	 * @param postId the post ID
	 * @param userId the author's user ID
	 * @param request the creation request containing content
	 * @return the created comment DTO
	 */
	@PostMapping("/post/{postId}/user/{userId}")
	public ResponseEntity<CommentDTO> addComment(
			@PathVariable Long postId,
			@PathVariable Long userId,
			@Valid @RequestBody CreateCommentRequest request) {
		return ResponseEntity.ok(commentService.addComment(postId, userId, request));
	}
}
