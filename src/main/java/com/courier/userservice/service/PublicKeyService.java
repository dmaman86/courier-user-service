package com.courier.userservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.courier.userservice.objects.dto.AuthInfoDto;

@Component
public class PublicKeyService {

  @Autowired private RedisService redisService;

  @KafkaListener(topics = "public-key", groupId = "user-service-group")
  public void listenPublicKey(AuthInfoDto authInfoDto) {
    redisService.saveKeyValues(authInfoDto.getPublicKey(), authInfoDto.getAuthServiceSecret());
  }
}
