package com.algolens.algo_lens.exception;


import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(ExternalApiException.class)
    public ResponseEntity<String> handleExternalApiError(ExternalApiException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAll(Exception e, HttpServletRequest request) {
        log.error("Unhandled exception on {}: {}", request.getRequestURI(), e.getMessage(), e);
        return ResponseEntity.status(500)
                .body(Map.of("error", e.getClass().getSimpleName(), "message", e.getMessage()));
    }

    @ExceptionHandler(CfAuthException.class)
    public ResponseEntity<String> handleCfAuthException(CfAuthException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(e.getMessage());
    }

    @ExceptionHandler(CodeFetchException.class)
    public ResponseEntity<String> handleCodeFetchException(CodeFetchException e) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(e.getMessage());
    }

    @ExceptionHandler(NoCommonContestsException.class)
    public ResponseEntity<String> handleNoCommonContestsException(NoCommonContestsException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(e.getMessage());
    }

    @ExceptionHandler(AiServiceException.class)
    public ResponseEntity<String> handleAiService(AiServiceException e) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(e.getMessage());
    }
}
