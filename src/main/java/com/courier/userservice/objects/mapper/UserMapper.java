package com.courier.userservice.objects.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.courier.userservice.objects.dto.UserDto;
import com.courier.userservice.objects.entity.User;

@Mapper(
    componentModel = "spring",
    uses = {RoleMapper.class})
public interface UserMapper {

  @Mapping(source = "roles", target = "roles")
  UserDto toDto(User user);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "disabledAt", ignore = true)
  @Mapping(target = "enabled", ignore = true)
  @Mapping(source = "roles", target = "roles")
  User toEntity(UserDto userDto);
}
