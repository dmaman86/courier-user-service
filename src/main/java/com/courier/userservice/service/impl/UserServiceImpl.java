package com.courier.userservice.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.courier.userservice.exception.EntityNotFoundException;
import com.courier.userservice.manager.UserManager;
import com.courier.userservice.objects.criteria.UserCriteria;
import com.courier.userservice.objects.dto.UserDto;
import com.courier.userservice.objects.entity.User;
import com.courier.userservice.objects.mapper.UserMapper;
import com.courier.userservice.repository.UserRepository;
import com.courier.userservice.service.UserService;

@Service
public class UserServiceImpl implements UserService {

  @Autowired private UserRepository userRepository;

  @Autowired private UserMapper userMapper;

  @Autowired private UserManager userManager;

  @Override
  public Page<UserDto> getUsers(Pageable pageable) {
    return userRepository.findByEnabledTrue(pageable).map(userMapper::toDto);
  }

  @Override
  public List<UserDto> getUsers() {
    return userRepository.findByEnabledTrue().stream()
        .map(userMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  public UserDto getUserById(Long id) {
    return userManager.getUserById(id);
  }

  @Override
  public UserDto getUserByEmailOrPhone(String email, String phone) {
    Optional<User> user = userRepository.findByEmailOrPhoneNumberAndEnabledTrue(email, phone);

    return user.map(userMapper::toDto)
        .orElseThrow(() -> new EntityNotFoundException("User not found with email or phone"));
  }

  @Override
  public UserDto createUser(UserDto userDto) {
    return userManager.createUser(userDto);
  }

  @Override
  public UserDto updateUser(Long id, UserDto userDto) {
    return userManager.updateUser(id, userDto);
  }

  @Override
  public void disableUser(Long id) {
    userManager.disableUser(id);
  }

  @Override
  public Page<UserDto> searchUsers(String search, Pageable pageable) {
    if (search == null || search.trim().isEmpty()) {
      return Page.empty();
    }

    Specification<User> specification = UserCriteria.containsTextInAttributes(search);
    Page<User> users = userRepository.findAll(specification, pageable);
    return users.map(userMapper::toDto);
  }
}
