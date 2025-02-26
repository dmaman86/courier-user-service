package com.courier.userservice.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.courier.userservice.exception.PublicKeyException;
import com.courier.userservice.objects.dto.UserContext;
import com.courier.userservice.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired private JwtService jwtService;

  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      jwtService.getPublicKey();
      String token = extractTokenFromCookies(request);
      if (token != null && jwtService.isTokenValid(token)) {
        UserContext user = jwtService.getUserContext(token);
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(user, null, user.getRoles());
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (PublicKeyException e) {
      throw new PublicKeyException(e.getMessage());
    }
    filterChain.doFilter(request, response);
  }

  private String extractTokenFromCookies(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if ("accessToken".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    return null;
  }
}
