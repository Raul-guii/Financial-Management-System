package com.raul.backend.controller;

import com.raul.backend.config.exception.GatewayException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GatewayException.class)
    public ResponseEntity<?> handleGateway(GatewayException ex) {
        return ResponseEntity
                .status(503)
                .body(Map.of("error", ex.getMessage()));
    }
}