package com.courier.userservice.config.filters;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.courier.userservice.exception.UnauthorizedException;
import com.courier.userservice.service.RedisService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {

  @Autowired private RedisService redisService;

  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (request.getRequestURI().equals("/api/user/find-by-email-or-phone")) {
      String requestApiKey = request.getHeader("X-Api-Key");
      String storedApiKey = redisService.getAuthServiceSecret();

      if (storedApiKey == null || !storedApiKey.equals(requestApiKey)) {
        throw new UnauthorizedException("Invalid API Key for auth-service");
      }
    }
    filterChain.doFilter(request, response);
  }
}
