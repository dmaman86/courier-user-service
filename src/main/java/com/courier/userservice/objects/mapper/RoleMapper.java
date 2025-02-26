package com.courier.userservice.objects.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.courier.userservice.objects.dto.RoleDto;
import com.courier.userservice.objects.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

  RoleDto toDto(Role role);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "disabledAt", ignore = true)
  @Mapping(target = "enabled", ignore = true)
  Role toEntity(RoleDto roleDto);
}
