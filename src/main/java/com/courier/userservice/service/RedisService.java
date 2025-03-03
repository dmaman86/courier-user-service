package com.courier.userservice.service;

public interface RedisService {

  void saveKeyValues(String publicKey, String authServiceSecret);

  String getPublicKey();

  String getAuthServiceSecret();

  boolean hasValidPublicKey();
}
