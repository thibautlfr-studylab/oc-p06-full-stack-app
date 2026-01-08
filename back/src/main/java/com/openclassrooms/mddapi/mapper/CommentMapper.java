package com.openclassrooms.mddapi.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.openclassrooms.mddapi.dto.CommentDTO;
import com.openclassrooms.mddapi.model.Comment;

/**
 * MapStruct mapper for Comment entity.
 */
@Mapper(componentModel = "spring")
public interface CommentMapper {

	@Mapping(source = "author.id", target = "authorId")
	@Mapping(source = "author.username", target = "authorUsername")
	@Mapping(source = "post.id", target = "postId")
	CommentDTO toDTO(Comment comment);

	@Mapping(source = "authorId", target = "author.id")
	@Mapping(source = "postId", target = "post.id")
	@Mapping(target = "author.username", ignore = true)
	@Mapping(target = "author.email", ignore = true)
	@Mapping(target = "author.password", ignore = true)
	@Mapping(target = "author.createdAt", ignore = true)
	@Mapping(target = "author.updatedAt", ignore = true)
	@Mapping(target = "post.title", ignore = true)
	@Mapping(target = "post.content", ignore = true)
	@Mapping(target = "post.author", ignore = true)
	@Mapping(target = "post.topic", ignore = true)
	@Mapping(target = "post.createdAt", ignore = true)
	@Mapping(target = "post.updatedAt", ignore = true)
	Comment toEntity(CommentDTO dto);

	List<CommentDTO> toDTOList(List<Comment> comments);
}
