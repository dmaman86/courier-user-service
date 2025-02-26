package com.courier.userservice.objects.dto;

import java.util.Set;

import org.springframework.security.core.GrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserContext {

  private Long id;
  private String fullName;
  private String email;
  private String phoneNumber;
  private Set<GrantedAuthority> roles;
}
