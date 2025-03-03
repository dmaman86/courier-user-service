package com.courier.userservice.service;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.courier.userservice.exception.PublicKeyException;
import com.courier.userservice.objects.dto.UserContext;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {

  @Autowired private RedisService redisService;

  public boolean isTokenValid(String token) {
    return extractExpiration(token).after(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public UserContext getUserContext(String token) {
    Claims claims = parseTokenClaims(token);

    Collection<? extends GrantedAuthority> authorities =
        ((List<?>) claims.get("roles"))
            .stream()
                .map(Object::toString)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

    return UserContext.builder()
        .id(Long.parseLong(claims.get("id").toString()))
        .fullName(claims.get("fullName", String.class))
        .phoneNumber(claims.get("phoneNumber", String.class))
        .email(claims.get("email", String.class))
        .authorities(authorities)
        .build();
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = parseTokenClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims parseTokenClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getPublicKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private PublicKey getPublicKey() {
    String publicKeyStr = redisService.getPublicKey();
    if (publicKeyStr == null) {
      throw new PublicKeyException("Public key has not been set yet.");
    }

    try {
      byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);
      X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      return keyFactory.generatePublic(keySpec);

    } catch (Exception e) {
      throw new RuntimeException("Error loading public key from Redis", e);
    }
  }
}
