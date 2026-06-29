package com.algolens.algo_lens.controller;

import com.algolens.algo_lens.auth.entities.User;
import com.algolens.algo_lens.auth.services.RateLimiterService;
import com.algolens.algo_lens.dtos.verification.ApiResponse;
import com.algolens.algo_lens.dtos.verification.OtpVerificationRequest;
import com.algolens.algo_lens.dtos.verification.PhoneVerificationRequest;
import com.algolens.algo_lens.services.VerificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
@Slf4j
public class VerificationController {

    private final VerificationService verificationService;
    private final RateLimiterService rateLimiterService;

    @PostMapping("/send-otp")
    public ResponseEntity<ApiResponse> sendOtp(
            @AuthenticationPrincipal User user,
            @RequestBody PhoneVerificationRequest request,
            HttpServletRequest httpRequest) {

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("UNAUTHORIZED", "Authentication required"));
        }

        if (request == null || isBlank(request.getPhoneNumber())) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("INVALID_REQUEST", "Phone number is required"));
        }

        try {
            String ip = extractIp(httpRequest);
            rateLimiterService.checkAndRecordPhone(request.getPhoneNumber(), ip);

            verificationService.generateAndSendOtp(user, request.getPhoneNumber());
            log.info("OTP sent for user: {}", user.getEmail());
            return ResponseEntity.ok(ApiResponse.success("OTP_SENT", "OTP sent successfully to phone and email"));

        } catch (com.algolens.algo_lens.auth.exception.RateLimitExceededException e) {
            log.warn("Rate limit exceeded in sendOtp for user {}: {}", user.getEmail(), e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(ApiResponse.error("RATE_LIMIT_EXCEEDED", e.getMessage()));

        } catch (IllegalArgumentException e) {
            log.warn("Invalid input in sendOtp for user {}: {}", user.getEmail(), e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("INVALID_INPUT", e.getMessage()));

        } catch (Exception e) {
            log.error("Unexpected error in sendOtp for user {}: {}", user.getEmail(), e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("SERVER_ERROR", "Something went wrong. Please try again."));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(
            @AuthenticationPrincipal User user,
            @RequestBody OtpVerificationRequest request) {

        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("UNAUTHORIZED", "Authentication required"));
        }

        if (request == null || isBlank(request.getOtp())) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("INVALID_REQUEST", "OTP is required"));
        }

        try {
            boolean isVerified = verificationService.verifyOtp(user, request.getOtp());

            if (isVerified) {
                log.info("OTP verified for user: {}", user.getEmail());
                return ResponseEntity.ok(ApiResponse.success("OTP_VERIFIED", "Phone number successfully verified"));
            }

            log.warn("OTP verification failed for user: {}", user.getEmail());
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error("OTP_INVALID", "Invalid or expired OTP"));

        } catch (Exception e) {
            log.error("Unexpected error in verifyOtp for user {}: {}", user.getEmail(), e.getMessage(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("SERVER_ERROR", "Something went wrong. Please try again."));
        }
    }


    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String extractIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

}