package com.algolens.algo_lens.services;

import com.algolens.algo_lens.auth.entities.User;
import com.algolens.algo_lens.auth.exception.InvalidTokenException;
import com.algolens.algo_lens.auth.exception.TokenExpiredException;
import com.algolens.algo_lens.auth.repositories.UserRepository;
import com.algolens.algo_lens.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationService {

    private final StringRedisTemplate redisTemplate;
    private final TwilioService twilioService;
    private final UserRepository userRepository;

    private static final String OTP_PREFIX = "phone_otp:";
    private static final long OTP_TTL_MINUTES = 5;

    @Transactional
    public void generateAndSendOtp(User user, String phoneNumber) {
        if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new InvalidTokenException("Invalid user data");
        }
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            throw new InvalidTokenException("Phone number cannot be empty");
        }

        log.debug("Generating OTP for user: {}", user.getEmail());
        String otp = String.format("%06d", new Random().nextInt(999999));

        try {
            redisTemplate.opsForValue().set(
                    OTP_PREFIX + user.getEmail(),
                    otp,
                    OTP_TTL_MINUTES,
                    TimeUnit.MINUTES
            );
            log.debug("OTP stored in Redis for user: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Redis operation failed: {}", e.getMessage(), e);
            throw new ExternalApiException("Failed to generate OTP. Please try again.");
        }

        try {
            user.setPhoneNumber(phoneNumber);
            userRepository.save(user);
            log.debug("Phone number updated for user: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Database operation failed: {}", e.getMessage(), e);
            try {
                redisTemplate.delete(OTP_PREFIX + user.getEmail());
                log.debug("Rolled back Redis OTP due to database failure");
            } catch (Exception rollbackEx) {
                log.error("Failed to rollback Redis: {}", rollbackEx.getMessage());
            }
            throw new ExternalApiException("Failed to save phone number. Please try again.");
        }

        try {
            String message = String.format("Your AlgoLens verification code is: %s", otp);
            twilioService.sendSms(phoneNumber, message);
            log.info("SMS sent successfully to {}", phoneNumber);
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage(), e);
            try {
                redisTemplate.delete(OTP_PREFIX + user.getEmail());
                user.setPhoneNumber(null);
                userRepository.save(user);
                log.debug("Rolled back Redis OTP and phone number due to SMS failure");
            } catch (Exception rollbackEx) {
                log.error("Failed to rollback after SMS failure: {}", rollbackEx.getMessage());
            }
            throw new ExternalApiException("Failed to send OTP. Please check your phone number and try again.");
        }

        log.info("OTP generation completed for user: {}", user.getEmail());
    }

    @Transactional
    public boolean verifyOtp(User user, String otp) {
        if (user == null || user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new InvalidTokenException("Invalid user data");
        }
        if (otp == null || otp.isEmpty()) {
            throw new InvalidTokenException("OTP cannot be empty");
        }

        log.debug("Verifying OTP for user: {}", user.getEmail());

        String savedOtp;
        try {
            savedOtp = redisTemplate.opsForValue().get(OTP_PREFIX + user.getEmail());
        } catch (Exception e) {
            log.error("Redis operation failed: {}", e.getMessage(), e);
            throw new ExternalApiException("Failed to verify OTP. Please try again.");
        }

        if (savedOtp == null) {
            log.warn("OTP not found for user: {} (expired or never sent)", user.getEmail());
            throw new TokenExpiredException("OTP expired or not found. Please request a new OTP.");
        }

        if (!savedOtp.equals(otp)) {
            log.warn("Invalid OTP attempt for user: {}", user.getEmail());
            throw new InvalidTokenException("Invalid OTP. Please try again.");
        }

        log.debug("OTP verified successfully for user: {}", user.getEmail());

        try {
            user.setPhoneVerified(true);
            userRepository.save(user);
            log.debug("User marked as phone verified: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Database operation failed: {}", e.getMessage(), e);
            throw new ExternalApiException("Failed to verify phone. Please try again.");
        }

        try {
            redisTemplate.delete(OTP_PREFIX + user.getEmail());
            log.debug("OTP deleted from Redis after successful verification");
        } catch (Exception e) {
            log.warn("Failed to delete OTP from Redis (non-critical): {}", e.getMessage());
        }

        log.info("Phone verification completed successfully for user: {}", user.getEmail());
        return true;
    }
}