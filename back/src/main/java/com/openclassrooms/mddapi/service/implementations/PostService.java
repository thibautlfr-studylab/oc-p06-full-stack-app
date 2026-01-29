package com.openclassrooms.mddapi.service.implementations;

import com.openclassrooms.mddapi.dto.PostDTO;
import com.openclassrooms.mddapi.dto.request.CreatePostRequest;
import com.openclassrooms.mddapi.exception.ResourceNotFoundException;
import com.openclassrooms.mddapi.mapper.PostMapper;
import com.openclassrooms.mddapi.model.Post;
import com.openclassrooms.mddapi.model.Topic;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.PostRepository;
import com.openclassrooms.mddapi.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.repository.TopicRepository;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.service.interfaces.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Post operations.
 */
@Service
@RequiredArgsConstructor
public class PostService implements IPostService {

  private final PostRepository postRepository;
  private final UserRepository userRepository;
  private final TopicRepository topicRepository;
  private final SubscriptionRepository subscriptionRepository;
  private final PostMapper postMapper;

  @Override
  public List<PostDTO> getFeedForUser(Long userId, boolean ascending) {
    List<Long> topicIds = subscriptionRepository.findByUserId(userId)
        .stream()
        .map(sub -> sub.getTopic().getId())
        .collect(Collectors.toList());

    if (topicIds.isEmpty()) {
      return List.of();
    }

    List<Post> posts = ascending
        ? postRepository.findByTopicIdInOrderByCreatedAtAsc(topicIds)
        : postRepository.findByTopicIdInOrderByCreatedAtDesc(topicIds);

    return postMapper.toDTOList(posts);
  }

  @Override
  public PostDTO getPostById(Long id) {
    return postRepository.findByIdWithDetails(id)
        .map(postMapper::toDTO)
        .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
  }

  @Override
  @Transactional
  public PostDTO createPost(Long authorId, CreatePostRequest request) {
    User author = userRepository.findById(authorId)
        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + authorId));

    Topic topic = topicRepository.findById(request.getTopicId())
        .orElseThrow(() -> new ResourceNotFoundException("Topic not found with id: " + request.getTopicId()));

    Post post = Post.builder()
        .title(request.getTitle())
        .content(request.getContent())
        .author(author)
        .topic(topic)
        .build();

    return postMapper.toDTO(postRepository.save(post));
  }
}
