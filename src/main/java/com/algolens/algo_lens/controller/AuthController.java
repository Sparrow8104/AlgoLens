package com.algolens.algo_lens.controller;


import com.algolens.algo_lens.auth.services.AuthService;
import com.algolens.algo_lens.auth.services.PasswordResetService;
import com.algolens.algo_lens.auth.utils.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService authService, PasswordResetService passwordResetService) {
        this.authService = authService;
        this.passwordResetService = passwordResetService;
    }
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request,
                                           HttpServletRequest httpRequest) {
        String ip = extractIp(httpRequest);
        return ResponseEntity.ok(authService.register(request, ip));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        return ResponseEntity.ok(authService.verifyEmail(token));
    }


    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request,
                                              HttpServletRequest httpRequest) {
        String deviceId = httpRequest.getHeader("X-Device-Id");
        if (deviceId == null || deviceId.isBlank()) {
            deviceId = UUID.randomUUID().toString();
        }

        String userAgent = httpRequest.getHeader("User-Agent");
        String ip = extractIp(httpRequest);

        return ResponseEntity.ok(authService.login(request, deviceId, userAgent, ip));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.logout(request.refreshToken()));
    }

    @PostMapping("/logout-all")
    public ResponseEntity<String> logoutAll(@AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            throw new RuntimeException("Unauthorized");
        }
        return ResponseEntity.ok(authService.logoutAll(userDetails.getUsername()));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request,
            HttpServletRequest httpRequest) {

        String ip = extractIp(httpRequest);
        return ResponseEntity.ok(passwordResetService.forgotPassword(request, ip));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {

        return ResponseEntity.ok(passwordResetService.resetPassword(request));
    }
    @PostMapping("/resend-verification")
    public ResponseEntity<String> resendVerification(@RequestParam String email,
                                                     HttpServletRequest httpRequest) {
        String ip = extractIp(httpRequest);
        return ResponseEntity.ok(authService.resendVerification(email, ip));
    }



    private String extractIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

}
