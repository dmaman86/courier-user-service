package com.courier.userservice.exception;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.courier.userservice.objects.dto.ErrorLogDto;
import com.courier.userservice.service.ErrorLogService;

import io.jsonwebtoken.JwtException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @Autowired private ErrorLogService errorLogService;

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorLogDto> handleEntityNotFoundException(
      EntityNotFoundException ex, WebRequest request) {
    return reportError(ex, "Entity not found", HttpStatus.NOT_FOUND, request);
  }

  @ExceptionHandler(EntityExistsException.class)
  public ResponseEntity<ErrorLogDto> handleEntityExistsException(
      EntityExistsException ex, WebRequest request) {
    return reportError(ex, "Entity already exists", HttpStatus.CONFLICT, request);
  }

  @ExceptionHandler(TokenValidationException.class)
  public ResponseEntity<ErrorLogDto> handleTokenValidationException(
      TokenValidationException ex, WebRequest request) {
    return reportError(ex, "Invalid token", HttpStatus.UNAUTHORIZED, request);
  }

  @ExceptionHandler(PublicKeyException.class)
  public ResponseEntity<ErrorLogDto> handlePublicKeyNotAvailableException(
      PublicKeyException ex, WebRequest request) {
    return reportError(ex, "Public key not available", HttpStatus.SERVICE_UNAVAILABLE, request);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorLogDto> handleGenericException(Exception ex, WebRequest request) {
    return reportError(
        ex, "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR, request);
  }

  @ExceptionHandler(JwtException.class)
  public ResponseEntity<ErrorLogDto> handleJwtException(JwtException ex, WebRequest request) {
    return reportError(ex, "JWT error", HttpStatus.UNAUTHORIZED, request);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorLogDto> handleAccessDeniedException(
      AccessDeniedException ex, WebRequest request) {
    return reportError(ex, "Access denied", HttpStatus.FORBIDDEN, request);
  }

  private ResponseEntity<ErrorLogDto> reportError(
      Exception ex, String message, HttpStatus status, WebRequest request) {
    ErrorLogDto errorLog =
        ErrorLogDto.builder()
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(message)
            .exception(ex.getClass().getName())
            .path(request.getDescription(false))
            .build();
    logger.error("Error: {}", errorLog);

    errorLogService.sendErrorLog(errorLog);

    return new ResponseEntity<>(errorLog, status);
  }
}
