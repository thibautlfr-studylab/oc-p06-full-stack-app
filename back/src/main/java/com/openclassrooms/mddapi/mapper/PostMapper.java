package com.openclassrooms.mddapi.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.openclassrooms.mddapi.dto.PostDTO;
import com.openclassrooms.mddapi.model.Post;

/**
 * MapStruct mapper for Post entity.
 */
@Mapper(componentModel = "spring")
public interface PostMapper {

	@Mapping(source = "author.id", target = "authorId")
	@Mapping(source = "author.username", target = "authorUsername")
	@Mapping(source = "topic.id", target = "topicId")
	@Mapping(source = "topic.name", target = "topicName")
	PostDTO toDTO(Post post);

	@Mapping(source = "authorId", target = "author.id")
	@Mapping(source = "topicId", target = "topic.id")
	@Mapping(target = "author.username", ignore = true)
	@Mapping(target = "author.email", ignore = true)
	@Mapping(target = "author.password", ignore = true)
	@Mapping(target = "author.createdAt", ignore = true)
	@Mapping(target = "author.updatedAt", ignore = true)
	@Mapping(target = "topic.name", ignore = true)
	@Mapping(target = "topic.description", ignore = true)
	Post toEntity(PostDTO dto);

	List<PostDTO> toDTOList(List<Post> posts);
}
