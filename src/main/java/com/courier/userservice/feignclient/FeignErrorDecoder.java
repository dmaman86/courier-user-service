package com.courier.userservice.feignclient;

import java.nio.file.AccessDeniedException;

import org.springframework.stereotype.Component;

import feign.Response;
import feign.codec.ErrorDecoder;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

  @Override
  public Exception decode(String methodKey, Response response) {
    String errorMessage =
        String.format(
            "Error in method %s with status %d: %s",
            methodKey, response.status(), response.reason());

    switch (response.status()) {
      case 400:
        return new RuntimeException("Bad Request: " + errorMessage);
      case 403:
        return new AccessDeniedException("Access Denied: " + errorMessage);
      case 404:
        return new EntityNotFoundException("Resource Not Found: " + errorMessage);
      case 409:
        return new EntityExistsException("Conflict Detected: " + errorMessage);
      case 500:
        return new RuntimeException("Internal Server Error: " + errorMessage);
      default:
        return new RuntimeException("Unexpected Error: " + errorMessage);
    }
  }
}
