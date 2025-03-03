package com.courier.userservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.courier.userservice.service.RedisService;

@Service
public class RedisServiceImpl implements RedisService {

  @Autowired private StringRedisTemplate redisTemplate;

  private static final String PUBLIC_KEY = "publicKey";
  private static final String AUTH_SERVICE = "authServiceSecret";

  @Override
  public void saveKeyValues(String publicKey, String authServiceSecret) {
    redisTemplate.opsForValue().set(PUBLIC_KEY, publicKey);
    redisTemplate.opsForValue().set(AUTH_SERVICE, authServiceSecret);
  }

  @Override
  public String getPublicKey() {
    return redisTemplate.opsForValue().get(PUBLIC_KEY);
  }

  @Override
  public String getAuthServiceSecret() {
    return redisTemplate.opsForValue().get(AUTH_SERVICE);
  }

  @Override
  public boolean hasValidPublicKey() {
    return redisTemplate.hasKey(PUBLIC_KEY);
  }
}
