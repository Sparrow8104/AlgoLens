package com.algolens.algo_lens.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<Map<String,Object>> handleTokenRefresh(TokenRefreshException ex) {
    return error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String,Object>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        return error(HttpStatus.CONFLICT, ex.getMessage());
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleBadCredentials(BadCredentialsException ex) {
        return error(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFound(UsernameNotFoundException ex) {
        return error(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }
    private ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "error", message,
                "status", status.value(),
                "timestamp", Instant.now().toString()
        ));
    }
}
