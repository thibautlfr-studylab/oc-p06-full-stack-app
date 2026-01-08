package com.openclassrooms.mddapi.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.openclassrooms.mddapi.dto.TopicDTO;
import com.openclassrooms.mddapi.model.Topic;

/**
 * MapStruct mapper for Topic entity.
 */
@Mapper(componentModel = "spring")
public interface TopicMapper {

	TopicDTO toDTO(Topic topic);

	Topic toEntity(TopicDTO dto);

	List<TopicDTO> toDTOList(List<Topic> topics);
}
