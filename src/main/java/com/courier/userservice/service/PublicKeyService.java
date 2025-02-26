package com.courier.userservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.courier.userservice.objects.dto.PublicKeyDto;

@Component
public class PublicKeyService {

  @Autowired private JwtService jwtService;

  @KafkaListener(topics = "public-key", groupId = "user-service-group")
  public void listenPublicKey(PublicKeyDto publicKeyDto) {
    jwtService.updatePublicKey(publicKeyDto);
  }
}
