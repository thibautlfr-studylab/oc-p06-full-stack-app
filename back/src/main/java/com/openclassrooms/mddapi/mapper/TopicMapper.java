package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.TopicDTO;
import com.openclassrooms.mddapi.model.Topic;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * MapStruct mapper for Topic entity.
 */
@Mapper(componentModel = "spring")
public interface TopicMapper {

  TopicDTO toDTO(Topic topic);

  Topic toEntity(TopicDTO dto);

  List<TopicDTO> toDTOList(List<Topic> topics);
}
