package com.courier.userservice.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.courier.userservice.objects.dto.ContactDto;

@FeignClient(name = "courier-resource-service", configuration = FeignClientConfig.class)
public interface ResourceFeignClient {

  @PostMapping("/api/courier/resource/contact")
  ContactDto createContact(@RequestBody ContactDto contactDto);

  @PostMapping("/api/courier/resource/contact/{id}")
  ContactDto updateContact(@PathVariable Long id, @RequestBody ContactDto contactDto);

  @DeleteMapping("/api/courier/resource/contact/{id}")
  void disableContact(@PathVariable Long id);

  @PostMapping("/api/courier/resource/contact/enable")
  ContactDto enableContact(@RequestBody ContactDto contactDto);

  @GetMapping("/api/courier/resource/contact/phone/{phoneNumber}")
  ContactDto getContactByPhone(@PathVariable String phoneNumber);
}
