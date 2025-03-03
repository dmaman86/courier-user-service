package com.courier.userservice.config.filters;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.courier.userservice.exception.PublicKeyException;
import com.courier.userservice.objects.dto.UserContext;
import com.courier.userservice.service.BlackListService;
import com.courier.userservice.service.JwtService;
import com.courier.userservice.service.RedisService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired private JwtService jwtService;

  @Autowired private RedisService redisService;

  @Autowired private BlackListService blackListService;

  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    if (!redisService.hasValidPublicKey()) {
      throw new PublicKeyException("Public key is not set in Redis");
    }

    String token = extractTokenFromCookies(request);
    if (token != null && jwtService.isTokenValid(token)) {
      UserContext user = jwtService.getUserContext(token);

      if (blackListService.isUserBlackListed(user.getId())) {
        SecurityContextHolder.clearContext();
        throw new AccessDeniedException("User is disabled");
      }

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
      SecurityContextHolder.getContext().setAuthentication(authentication);
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
