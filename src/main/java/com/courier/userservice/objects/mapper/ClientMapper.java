package com.courier.userservice.objects.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.courier.userservice.objects.dto.ClientDto;
import com.courier.userservice.objects.dto.ContactDto;
import com.courier.userservice.objects.dto.UserDto;
import com.courier.userservice.objects.entity.User;

@Mapper(
    componentModel = "spring",
    uses = {RoleMapper.class})
public interface ClientMapper {

  @Mapping(source = "office", target = "office")
  @Mapping(source = "branches", target = "branches")
  ContactDto clientToContactDto(ClientDto clientDto);

  @Mapping(source = "roles", target = "roles")
  UserDto clientToUserDto(ClientDto clientDto);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "disabledAt", ignore = true)
  @Mapping(target = "enabled", ignore = true)
  @Mapping(source = "roles", target = "roles")
  User clientToUser(ClientDto clientDto);

  @Mapping(source = "contactDto.office", target = "office")
  @Mapping(source = "contactDto.branches", target = "branches")
  @Mapping(source = "userDto.phoneNumber", target = "phoneNumber")
  @Mapping(source = "userDto.id", target = "id")
  @Mapping(source = "userDto.fullName", target = "fullName")
  ClientDto fromUserAndContact(UserDto userDto, ContactDto contactDto);
}
