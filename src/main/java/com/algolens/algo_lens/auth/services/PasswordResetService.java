package com.algolens.algo_lens.auth.services;

import com.algolens.algo_lens.auth.entities.PasswordResetToken;
import com.algolens.algo_lens.auth.entities.User;
import com.algolens.algo_lens.auth.exception.InvalidTokenException;
import com.algolens.algo_lens.auth.exception.TokenExpiredException;
import com.algolens.algo_lens.auth.repositories.PasswordResetTokenRepository;
import com.algolens.algo_lens.auth.repositories.RefreshTokenRepository;
import com.algolens.algo_lens.auth.repositories.UserRepository;
import com.algolens.algo_lens.auth.utils.ForgotPasswordRequest;
import com.algolens.algo_lens.auth.utils.ResetPasswordRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class PasswordResetService {

    private static final long RESET_TOKEN_EXPIRY_SECONDS = 900;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailRateLimiterService emailRateLimiterService;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository passwordResetTokenRepository,
                                RefreshTokenRepository refreshTokenRepository,
                                PasswordEncoder passwordEncoder,
                                EmailService emailService,
                                EmailRateLimiterService emailRateLimiterService) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.emailRateLimiterService = emailRateLimiterService;
    }

    @Transactional
    public String forgotPassword(ForgotPasswordRequest request, String ip) {
        emailRateLimiterService.checkAndRecord(request.email(), ip);

        userRepository.findByEmail(request.email()).ifPresent(user -> {
            if (user.isEmailVerified()) {
                issueResetToken(user);
            }
        });
        return "If that email is registered, a password reset link has been sent.";
    }

    @Transactional
    public String resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByToken(request.token())
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired reset token."));

        if (resetToken.isUsed()) {
            throw new InvalidTokenException("This reset link has already been used.");
        }

        if (resetToken.getExpiresAt().isBefore(Instant.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new TokenExpiredException("Reset link has expired. Please request a new one.");
        }

        User user = resetToken.getUser();

        if (passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from your current password.");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
        refreshTokenRepository.deleteByUser(user);

        return "Password reset successfully. Please log in with your new password.";
    }

    private void issueResetToken(User user) {
        String rawToken = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plusSeconds(RESET_TOKEN_EXPIRY_SECONDS);

        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByUser(user)
                .map(existing -> {
                    existing.setToken(rawToken);
                    existing.setExpiresAt(expiry);
                    existing.setUsed(false);
                    return existing;
                })
                .orElseGet(() -> PasswordResetToken.builder()
                        .token(rawToken)
                        .user(user)
                        .expiresAt(expiry)
                        .used(false)
                        .build());

        passwordResetTokenRepository.save(resetToken);
        emailService.sendPasswordResetEmail(user.getEmail(), rawToken);
    }
}