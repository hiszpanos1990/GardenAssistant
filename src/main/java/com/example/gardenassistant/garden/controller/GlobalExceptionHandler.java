package com.example.gardenassistant.garden.controller;

import com.example.gardenassistant.exception.*;
import com.example.gardenassistant.garden.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiError> userNotFound(UserNotFoundException e) {
        ApiError response = new ApiError(HttpStatus.NOT_FOUND.value(), e.getMessage());
        return ResponseEntity.status(response.status()).body(response);
    }

    @ExceptionHandler(GardenFullException.class)
    public ResponseEntity<ApiError> gardenFull(GardenFullException e) {
        ApiError response = new ApiError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return ResponseEntity.status(response.status()).body(response);
    }

    @ExceptionHandler(GardenNotFoundException.class)
    public ResponseEntity<ApiError> gardenNotFound(GardenNotFoundException e) {
        ApiError response = new ApiError(HttpStatus.NOT_FOUND.value(), e.getMessage());
        return ResponseEntity.status(response.status()).body(response);
    }

    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<ApiError> alreadyExist(AlreadyExistException e) {
        ApiError response = new ApiError(HttpStatus.CONFLICT.value(), e.getMessage());
        return ResponseEntity.status(response.status()).body(response);
    }

    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<ApiError> invalidCredential(InvalidCredentialException e) {
        ApiError response = new ApiError(HttpStatus.UNAUTHORIZED.value(), e.getMessage());
        return ResponseEntity.status(response.status()).body(response);
    }

}
