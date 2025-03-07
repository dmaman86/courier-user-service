package com.courier.userservice.config.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.courier.userservice.objects.dto.ErrorLogDto;
import com.courier.userservice.objects.dto.UserDto;

@Configuration
public class KafkaProducerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Bean
  public <T> ProducerFactory<String, T> producerFactory(Class<T> valueType) {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, ErrorLogDto> errorLogDtoKafkaTemplate() {
    return new KafkaTemplate<>(producerFactory(ErrorLogDto.class));
  }

  @Bean
  public KafkaTemplate<String, UserDto> userDtoKafkaTemplate() {
    return new KafkaTemplate<>(producerFactory(UserDto.class));
  }

  @Bean
  public KafkaTemplate<String, Long> longKafkaTemplate() {
    return new KafkaTemplate<>(producerFactory(Long.class));
  }
}
