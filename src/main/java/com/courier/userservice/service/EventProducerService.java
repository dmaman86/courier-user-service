package com.courier.userservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.courier.userservice.objects.dto.ErrorLogDto;
import com.courier.userservice.objects.dto.UserDto;

@Service
public class EventProducerService {

  @Autowired private KafkaTemplate<String, ErrorLogDto> errorLogDtoTemplate;

  @Autowired private KafkaTemplate<String, UserDto> userDtoKafkaTemplate;

  @Autowired private KafkaTemplate<String, Long> longKafkaTemplate;

  public void sendErrorLog(ErrorLogDto errorLogDto) {
    errorLogDtoTemplate.send("error-topic", errorLogDto);
  }

  public void sendUserCreated(UserDto userDto) {
    userDtoKafkaTemplate.send("user-created", userDto);
  }

  public void sendUserDeleted(Long userId) {
    longKafkaTemplate.send("user-disabled", userId);
  }
}
