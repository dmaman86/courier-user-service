package com.courier.userservice.manager;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.courier.userservice.objects.dto.RoleDto;
import com.courier.userservice.objects.entity.Role;
import com.courier.userservice.repository.RoleRepository;
import com.courier.userservice.repository.UserRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@Component
public class RoleManager {

  @Autowired private RoleRepository roleRepository;

  @Autowired private UserRepository userRepository;

  @Transactional
  public Role createRole(RoleDto roleDto) {
    if (roleRepository.existsByNameAndEnabledTrue(roleDto.getName())) {
      throw new EntityExistsException("Role already exists: " + roleDto.getName());
    }

    Role role = Role.builder().name(roleDto.getName()).enabled(true).build();

    return roleRepository.save(role);
  }

  @Transactional
  public Role updateRole(Long roleId, RoleDto roleDto) {
    Role role =
        roleRepository
            .findByIdAndEnabledTrue(roleId)
            .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleId));

    boolean exists = roleRepository.existsByNameAndEnabledTrue(roleDto.getName());
    if (!role.getId().equals(roleId) && exists) {
      throw new EntityExistsException("Role already exists: " + roleDto.getName());
    }

    role.setName(roleDto.getName());
    return roleRepository.save(role);
  }

  @Transactional
  public void disabledRole(Long roleId) {
    Role role =
        roleRepository
            .findByIdAndEnabledTrue(roleId)
            .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleId));

    long userCount = userRepository.countByRolesIdAndEnabledTrue(roleId);
    long usersWithOnlyThisRole = userRepository.countByRoles_IdAndRoles_Size(roleId, 1);

    if (userCount > 0 && usersWithOnlyThisRole > 0) {
      throw new IllegalStateException("Role has users associated with it: " + role.getName());
    }

    role.setEnabled(false);
    role.setDisabledAt(LocalDateTime.now());
    roleRepository.save(role);
  }
}
