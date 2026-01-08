package com.openclassrooms.mddapi.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.openclassrooms.mddapi.dto.UserDTO;
import com.openclassrooms.mddapi.model.User;

/**
 * MapStruct mapper for User entity.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

	UserDTO toDTO(User user);

	@Mapping(target = "password", ignore = true)
	User toEntity(UserDTO dto);

	List<UserDTO> toDTOList(List<User> users);
}
