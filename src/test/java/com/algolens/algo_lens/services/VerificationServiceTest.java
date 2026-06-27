package com.algolens.algo_lens.services;

import com.algolens.algo_lens.auth.entities.User;
import com.algolens.algo_lens.auth.exception.InvalidTokenException;
import com.algolens.algo_lens.auth.exception.TokenExpiredException;
import com.algolens.algo_lens.auth.repositories.UserRepository;
import com.algolens.algo_lens.exception.ExternalApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class VerificationServiceTest {

    private VerificationService verificationService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private TwilioService twilioService;

    @Mock
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        verificationService = new VerificationService(redisTemplate, twilioService, userRepository);

        user = new User();
        user.setEmail("test@example.com");
        user.setPhoneVerified(false);
    }

    @Test
    void generateAndSendOtp_Success() {

        verificationService.generateAndSendOtp(user, "+1234567890");

        verify(valueOperations).set(eq("phone_otp:test@example.com"), anyString(), eq(5L), eq(TimeUnit.MINUTES));

        verify(valueOperations).set(eq("pending_phone:test@example.com"), eq("+1234567890"), eq(5L), eq(TimeUnit.MINUTES));

        verify(twilioService).sendSms(eq("+1234567890"), anyString());

        assertNull(user.getPhoneNumber());
        assertFalse(user.isPhoneVerified());
    }

    @Test
    void generateAndSendOtp_EmptyPhoneNumber_ThrowsException() {

        assertThrows(InvalidTokenException.class, () -> 
                verificationService.generateAndSendOtp(user, "")
        );
    }

    @Test
    void verifyOtp_Success() {

        when(valueOperations.get("phone_otp:test@example.com")).thenReturn("123456");
        when(valueOperations.get("pending_phone:test@example.com")).thenReturn("+1234567890");
        when(valueOperations.get("phone_otp_attempts:test@example.com")).thenReturn("0");


        boolean result = verificationService.verifyOtp(user, "123456");

        assertTrue(result);

        assertEquals("+1234567890", user.getPhoneNumber());
        assertTrue(user.isPhoneVerified());
        verify(userRepository).save(user);


        verify(redisTemplate).delete("phone_otp:test@example.com");
        verify(redisTemplate).delete("pending_phone:test@example.com");
        verify(redisTemplate).delete("phone_otp_attempts:test@example.com");
    }

    @Test
    void verifyOtp_WrongOtp_IncrementsAttempts() {
        when(valueOperations.get("phone_otp:test@example.com")).thenReturn("123456");
        when(valueOperations.get("phone_otp_attempts:test@example.com")).thenReturn("2");


        assertThrows(InvalidTokenException.class, () ->
                verificationService.verifyOtp(user, "000000")
        );

        verify(valueOperations).increment("phone_otp_attempts:test@example.com");

        assertNull(user.getPhoneNumber());
        assertFalse(user.isPhoneVerified());
    }

    @Test
    void verifyOtp_MaxAttemptsExceeded_LocksOut() {
        when(valueOperations.get("phone_otp:test@example.com")).thenReturn("123456");

        when(valueOperations.get("phone_otp_attempts:test@example.com")).thenReturn("5");


        assertThrows(InvalidTokenException.class, () ->
                verificationService.verifyOtp(user, "123456")
        );


        verify(redisTemplate).delete("phone_otp:test@example.com");
        verify(redisTemplate).delete("pending_phone:test@example.com");
        verify(redisTemplate).delete("phone_otp_attempts:test@example.com");
    }
}
