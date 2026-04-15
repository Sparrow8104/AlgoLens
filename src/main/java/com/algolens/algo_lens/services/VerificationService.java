package com.algolens.algo_lens.services;

import com.algolens.algo_lens.auth.entities.User;
import com.algolens.algo_lens.auth.repositories.UserRepository;
import com.algolens.algo_lens.auth.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationService {

    private final StringRedisTemplate redisTemplate;
    private final TwilioService twilioService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    private static final String OTP_PREFIX = "phone_otp:";
    private static final long OTP_TTL_MINUTES = 5;

    public void generateAndSendOtp(User user, String phoneNumber) {
        String otp = String.format("%06d", new Random().nextInt(999999));
        
        // Store in Redis
        redisTemplate.opsForValue().set(OTP_PREFIX + user.getEmail(), otp, OTP_TTL_MINUTES, TimeUnit.MINUTES);

        // Update phone number temporarily (we verify it in the next step)
        user.setPhoneNumber(phoneNumber);
        userRepository.save(user);

        // Send via Twilio
        twilioService.sendSms(phoneNumber, "Your AlgoLens verification code is: " + otp);

        // Also send via Email just in case Twilio SMS is slow
        emailService.sendPhoneVerificationOtpEmail(user.getEmail(), otp, OTP_TTL_MINUTES);
        
        log.info("Sent OTP for user: {}", user.getEmail());
    }

    public boolean verifyOtp(User user, String otp) {
        String savedOtp = redisTemplate.opsForValue().get(OTP_PREFIX + user.getEmail());
        if (savedOtp != null && savedOtp.equals(otp)) {
            user.setPhoneVerified(true);
            userRepository.save(user);
            redisTemplate.delete(OTP_PREFIX + user.getEmail());
            log.info("Successfully verified phone for user: {}", user.getEmail());
            return true;
        }
        log.warn("Invalid OTP for user: {}", user.getEmail());
        return false;
    }
}
