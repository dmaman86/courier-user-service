package com.courier.userservice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.courier.userservice.objects.dto.UserDto;
import com.courier.userservice.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

  @Autowired private UserService userService;

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @GetMapping
  public ResponseEntity<Page<UserDto>> getAllUsers(Pageable pageable) {
    return ResponseEntity.ok(userService.getUsers(pageable));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @GetMapping("/all")
  public ResponseEntity<List<UserDto>> getAllUsers() {
    return ResponseEntity.ok(userService.getUsers());
  }

  @PreAuthorize(
      "hasAnyAuthority('ROLE_ADMIN', 'ROLE_COURIER') or #userId == authentication.principal.id")
  @GetMapping("/{userId}")
  public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
    return ResponseEntity.ok(userService.getUserById(userId));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @PostMapping
  public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
    return ResponseEntity.ok(userService.createUser(userDto));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @PutMapping("/{userId}")
  public ResponseEntity<UserDto> updateUser(
      @PathVariable Long userId, @RequestBody UserDto userDto) {
    return ResponseEntity.ok(userService.updateUser(userId, userDto));
  }

  @PreAuthorize("hasAuthority('ROLE_ADMIN')")
  @DeleteMapping("/{userId}")
  public ResponseEntity<Void> disableUser(@PathVariable Long userId) {
    userService.disableUser(userId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/search")
  public ResponseEntity<Page<UserDto>> searchUsers(@RequestParam String search, Pageable pageable) {
    return ResponseEntity.ok(userService.searchUsers(search, pageable));
  }
}
