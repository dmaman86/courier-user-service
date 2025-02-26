package com.courier.userservice.manager;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.courier.userservice.feignclient.ResourceFeignClient;
import com.courier.userservice.objects.dto.ClientDto;
import com.courier.userservice.objects.dto.ContactDto;
import com.courier.userservice.objects.dto.RoleDto;
import com.courier.userservice.objects.dto.UserDto;
import com.courier.userservice.objects.entity.User;
import com.courier.userservice.objects.mapper.ClientMapper;
import com.courier.userservice.objects.mapper.UserMapper;
import com.courier.userservice.repository.RoleRepository;
import com.courier.userservice.repository.UserRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@Component
public class UserManager {

  @Autowired private UserRepository userRepository;

  @Autowired private RoleRepository roleRepository;

  @Autowired private UserMapper userMapper;

  @Autowired private ClientMapper clientMapper;

  @Autowired private ResourceFeignClient resourceFeignClient;

  @Transactional(readOnly = true)
  public UserDto getUserById(Long userId) {
    User user =
        userRepository
            .findByIdAndEnabledTrue(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

    boolean isClient =
        user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_CLIENT"));

    if (isClient) return getClientDto(user);

    return userMapper.toDto(user);
  }

  @Transactional
  public UserDto createUser(UserDto userDto) {
    validateUniqueFields(userDto);
    validateRoles(userDto);

    if (userDto instanceof ClientDto clientDto) {
      createClientContact(clientDto);
    }
    User user = userMapper.toEntity(userDto);
    return userMapper.toDto(userRepository.save(user));
  }

  @Transactional
  public UserDto updateUser(Long userId, UserDto userDto) {
    User existingUser =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

    validateUpdatedField(userDto, existingUser);
    validateRoles(userDto);

    handleClientRoleChange(existingUser, userDto);

    existingUser.setFullName(userDto.getFullName());
    existingUser.setEmail(userDto.getEmail());
    existingUser.setPhoneNumber(userDto.getPhoneNumber());
    existingUser.getRoles().clear();
    existingUser.getRoles().addAll(userMapper.toEntity(userDto).getRoles());
    return userMapper.toDto(userRepository.save(existingUser));
  }

  @Transactional
  public void disableUser(Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

    if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_CLIENT"))) {
      disableClient(user);
    }

    user.setEnabled(false);
    user.setDisabledAt(LocalDateTime.now());
    userRepository.save(user);
  }

  private ClientDto getClientDto(User user) {
    try {
      ContactDto contactDto = resourceFeignClient.getContactByPhone(user.getPhoneNumber());
      return clientMapper.fromUserAndContact(userMapper.toDto(user), contactDto);
    } catch (Exception e) {
      throw new RuntimeException("Failed to get client: " + e.getMessage());
    }
  }

  private void validateUniqueFields(UserDto userDto) {

    userRepository
        .findByEmailOrPhoneNumberAndEnabledTrue(userDto.getEmail(), userDto.getPhoneNumber())
        .ifPresent(
            existingUser -> {
              throw new EntityExistsException(
                  "User already exists: " + userDto.getEmail() + " or " + userDto.getPhoneNumber());
            });
  }

  private void validateUpdatedField(UserDto userDto, User existingUser) {
    userRepository
        .findByEmailOrPhoneNumberAndEnabledTrue(userDto.getEmail(), userDto.getPhoneNumber())
        .ifPresent(
            userDb -> {
              if (!userDb.getId().equals(existingUser.getId())) {
                throw new EntityExistsException(
                    "User already exists: "
                        + userDto.getEmail()
                        + " or "
                        + userDto.getPhoneNumber());
              }
            });
  }

  private void handleClientRoleChange(User user, UserDto userDto) {
    boolean wasClient =
        user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_CLIENT"));
    boolean isNowClient =
        userDto.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_CLIENT"));

    if (!wasClient && isNowClient) {
      createClientContact((ClientDto) userDto);
    } else if (wasClient && !isNowClient) {
      disableClient(user);
    }
  }

  private void createClientContact(ClientDto clientDto) {
    ContactDto contactDto = clientMapper.clientToContactDto(clientDto);
    try {
      resourceFeignClient.createContact(contactDto);
    } catch (Exception e) {
      throw new RuntimeException("Failed to create contact: " + e.getMessage());
    }
  }

  private void disableClient(User user) {
    try {
      ContactDto contactDto = resourceFeignClient.getContactByPhone(user.getPhoneNumber());
      resourceFeignClient.disableContact(contactDto.getId());
    } catch (Exception e) {
      throw new RuntimeException("Failed to disable client: " + e.getMessage());
    }
  }

  private void validateRoles(UserDto userDto) {
    for (RoleDto roleDto : userDto.getRoles()) {
      if (!roleRepository.existsByNameAndEnabledTrue(roleDto.getName())) {
        throw new EntityNotFoundException("Role not found or disabled: " + roleDto.getName());
      }
    }
  }
}
