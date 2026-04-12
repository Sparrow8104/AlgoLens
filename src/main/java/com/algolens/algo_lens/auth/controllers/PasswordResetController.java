package com.algolens.algo_lens.auth.controllers;

import com.algolens.algo_lens.auth.services.PasswordResetService;
import com.algolens.algo_lens.auth.utils.ForgotPasswordRequest;
import com.algolens.algo_lens.auth.utils.ResetPasswordRequest;
import com.algolens.algo_lens.auth.utils.VerifyResetTokenRequest;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    public PasswordResetController(PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            @RequestHeader(value = "X-Forwarded-For", required = false) String forwardedFor,
            HttpServletRequest httpRequest) {

        String ip = forwardedFor != null
                ? forwardedFor.split(",")[0].trim()
                : httpRequest.getRemoteAddr();

        String message = passwordResetService.forgotPassword(request, ip);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/verify-reset-token")
    public ResponseEntity<Map<String, String>> verifyResetToken(
            @Valid @RequestBody VerifyResetTokenRequest request,
            HttpServletResponse response) {

        passwordResetService.verifyResetToken(request, response);
        return ResponseEntity.ok(Map.of("message", "Verification code sent to your email."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {

        String sessionJti = null;
        if (httpRequest.getCookies() != null) {
            sessionJti = Arrays.stream(httpRequest.getCookies())
                    .filter(c -> "reset_session".equals(c.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        String message = passwordResetService.resetPassword(request, sessionJti, httpResponse);
        return ResponseEntity.ok(Map.of("message", message));
    }
}