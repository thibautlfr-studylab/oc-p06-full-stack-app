package com.openclassrooms.mddapi.service.implementations;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openclassrooms.mddapi.dto.CommentDTO;
import com.openclassrooms.mddapi.dto.request.CreateCommentRequest;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.mapper.CommentMapper;
import com.openclassrooms.mddapi.model.Comment;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.CommentRepository;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.service.interfaces.ICommentService;

import lombok.RequiredArgsConstructor;

/**
 * Service implementation for Comment operations.
 */
@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final CommentMapper commentMapper;

	@Override
	public List<CommentDTO> getCommentsForPost(Long postId) {
		return commentMapper.toDTOList(commentRepository.findByPostIdWithAuthor(postId));
	}

	@Override
	@Transactional
	public CommentDTO addComment(Long postId, Long authorId, CreateCommentRequest request) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));

		User author = userRepository.findById(authorId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + authorId));

		Comment comment = Comment.builder()
				.content(request.getContent())
				.post(post)
				.author(author)
				.build();

		return commentMapper.toDTO(commentRepository.save(comment));
	}
}
