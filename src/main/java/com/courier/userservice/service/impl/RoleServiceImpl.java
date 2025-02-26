package com.courier.userservice.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.courier.userservice.exception.EntityNotFoundException;
import com.courier.userservice.manager.RoleManager;
import com.courier.userservice.objects.dto.RoleDto;
import com.courier.userservice.objects.entity.Role;
import com.courier.userservice.objects.mapper.RoleMapper;
import com.courier.userservice.repository.RoleRepository;
import com.courier.userservice.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

  @Autowired private RoleRepository roleRepository;

  @Autowired private RoleMapper roleMapper;

  @Autowired private RoleManager roleManager;

  @Override
  public List<RoleDto> getRoles() {
    return roleRepository.findByEnabledTrue().stream()
        .map(roleMapper::toDto)
        .collect(Collectors.toList());
  }

  @Override
  public RoleDto getRole(Long id) {
    return roleRepository
        .findById(id)
        .filter(Role::isEnabled)
        .map(roleMapper::toDto)
        .orElseThrow(() -> new EntityNotFoundException("Role not found"));
  }

  @Override
  public RoleDto createRole(RoleDto roleDto) {
    return roleMapper.toDto(roleManager.createRole(roleDto));
  }

  @Override
  public RoleDto updateRole(Long id, RoleDto roleDto) {
    return roleMapper.toDto(roleManager.updateRole(id, roleDto));
  }

  @Override
  public void disableRole(Long id) {
    roleManager.disabledRole(id);
  }
}
