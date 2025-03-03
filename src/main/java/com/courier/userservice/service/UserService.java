package com.courier.userservice.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.courier.userservice.objects.dto.UserDto;

public interface UserService {

  Page<UserDto> getUsers(Pageable pageable);

  List<UserDto> getUsers();

  UserDto getUserById(Long id);

  UserDto getUserByEmailOrPhone(String email, String phone);

  UserDto createUser(UserDto userDto);

  UserDto updateUser(Long id, UserDto userDto);

  void disableUser(Long id);

  Page<UserDto> searchUsers(String search, Pageable pageable);
}
