package com.courier.userservice.service;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.courier.userservice.exception.PublicKeyException;
import com.courier.userservice.objects.dto.PublicKeyDto;
import com.courier.userservice.objects.dto.UserContext;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class JwtService {

  private PublicKey publicKey;
  private long expirationTime;

  public void updatePublicKey(PublicKeyDto publicKeyDto) {
    try {
      byte[] keyBytes = Base64.getDecoder().decode(publicKeyDto.getPublicKey());
      X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      this.publicKey = keyFactory.generatePublic(keySpec);
      this.expirationTime = publicKeyDto.getExpirationTime();
    } catch (Exception e) {
      throw new RuntimeException("Error updating public key", e);
    }
  }

  public PublicKey getPublicKey() {
    if (publicKey == null) throw new PublicKeyException("Public key has not been set yet.");
    return publicKey;
  }

  public long getExpirationTime() {
    return expirationTime;
  }

  public boolean isTokenValid(String token) {
    return extractExpiration(token).after(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public UserContext getUserContext(String token) {
    Claims claims = parseTokenClaims(token);
    Set<String> roles = Set.of(claims.get("roles").toString().split(","));

    return UserContext.builder()
        .id(Long.parseLong(claims.get("id").toString()))
        .fullName(claims.get("fullName", String.class))
        .phoneNumber(claims.get("phoneNumber", String.class))
        .email(claims.get("email", String.class))
        .roles(roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet()))
        .build();
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = parseTokenClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims parseTokenClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(token).getBody();
  }
}
