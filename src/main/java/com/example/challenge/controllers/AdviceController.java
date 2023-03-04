package com.example.challenge.controllers;

import com.example.challenge.commons.ApiException;
import com.example.challenge.exceptions.RateLimitException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AdviceController {

    @ExceptionHandler(RateLimitException.class)
    protected ResponseEntity<String> handleBadRequestException(RateLimitException e) {

        ApiException apiException = new ApiException("rate_limit", e.getMessage(), e.getHttpStatusCode());
        return ResponseEntity.status(apiException.getStatusCode())
                             .contentType(MediaType.APPLICATION_JSON)
                             .body(apiException.toJson());
    }
}
