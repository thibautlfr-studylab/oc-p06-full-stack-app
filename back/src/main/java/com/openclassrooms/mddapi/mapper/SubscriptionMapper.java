package com.openclassrooms.mddapi.mapper;

import com.openclassrooms.mddapi.dto.SubscriptionDTO;
import com.openclassrooms.mddapi.model.Subscription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * MapStruct mapper for Subscription entity.
 */
@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

  @Mapping(source = "user.id", target = "userId")
  @Mapping(source = "topic.id", target = "topicId")
  @Mapping(source = "topic.name", target = "topicName")
  @Mapping(source = "topic.description", target = "topicDescription")
  SubscriptionDTO toDTO(Subscription subscription);

  @Mapping(source = "userId", target = "user.id")
  @Mapping(source = "topicId", target = "topic.id")
  @Mapping(target = "user.username", ignore = true)
  @Mapping(target = "user.email", ignore = true)
  @Mapping(target = "user.password", ignore = true)
  @Mapping(target = "user.createdAt", ignore = true)
  @Mapping(target = "user.updatedAt", ignore = true)
  @Mapping(target = "topic.name", ignore = true)
  @Mapping(target = "topic.description", ignore = true)
  Subscription toEntity(SubscriptionDTO dto);

  List<SubscriptionDTO> toDTOList(List<Subscription> subscriptions);
}
