package com.algolens.algo_lens.controllers;

import com.algolens.algo_lens.auth.entities.User;
import com.algolens.algo_lens.auth.repositories.UserRepository;
import com.algolens.algo_lens.services.VerificationService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
public class VerificationController {

    private final VerificationService verificationService;
    private final UserRepository userRepository;

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@AuthenticationPrincipal User user, @RequestBody PhoneVerificationRequest request) {
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        verificationService.generateAndSendOtp(user, request.getPhoneNumber());
        return ResponseEntity.ok("OTP sent successfully to phone and email.");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@AuthenticationPrincipal User user, @RequestBody OtpVerificationRequest request) {
        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        boolean isVerified = verificationService.verifyOtp(user, request.getOtp());
        if (isVerified) {
            return ResponseEntity.ok("Phone number successfully verified.");
        } else {
            return ResponseEntity.status(400).body("Invalid or expired OTP.");
        }
    }

    @Data
    public static class PhoneVerificationRequest {
        private String phoneNumber;
    }

    @Data
    public static class OtpVerificationRequest {
        private String otp;
    }
}
