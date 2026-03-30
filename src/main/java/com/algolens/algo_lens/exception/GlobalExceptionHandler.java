package com.algolens.algo_lens.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


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
    public ResponseEntity<String> handleGenericException(Exception e){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Something went wrong"+e.getMessage());
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
