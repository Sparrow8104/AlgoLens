package com.algolens.algo_lens.auth.services;

import com.algolens.algo_lens.auth.exception.TooManyRequestsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class LoginAttemptServiceTest {

    private LoginAttemptService loginAttemptService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        loginAttemptService = new LoginAttemptService(redisTemplate);
    }

    @Test
    void checkBlocked_NotBlocked() {

        when(redisTemplate.hasKey("login:blocked:127.0.0.1")).thenReturn(false);

        assertDoesNotThrow(() -> loginAttemptService.checkBlocked("127.0.0.1"));
    }

    @Test
    void checkBlocked_IsBlocked_ThrowsException() {

        when(redisTemplate.hasKey("login:blocked:127.0.0.1")).thenReturn(true);

        assertThrows(TooManyRequestsException.class, () ->
                loginAttemptService.checkBlocked("127.0.0.1")
        );
    }

    @Test
    void recordFailure_IncrementsAndSetsExpiry() {

        when(valueOperations.increment("login:attempts:127.0.0.1")).thenReturn(1L);

        loginAttemptService.recordFailure("127.0.0.1");

        verify(valueOperations).increment("login:attempts:127.0.0.1");
        verify(redisTemplate).expire("login:attempts:127.0.0.1", 900L, TimeUnit.SECONDS);
    }

    @Test
    void recordFailure_MaxAttemptsReached_BlocksIp() {

        when(valueOperations.increment("login:attempts:127.0.0.1")).thenReturn(5L);

        loginAttemptService.recordFailure("127.0.0.1");


        verify(valueOperations).set("login:blocked:127.0.0.1", "1", 900L, TimeUnit.SECONDS);
        verify(redisTemplate).delete("login:attempts:127.0.0.1");
    }

    @Test
    void clearFailures_DeletesKeys() {
        loginAttemptService.clearFailures("127.0.0.1");


        verify(redisTemplate).delete("login:attempts:127.0.0.1");
        verify(redisTemplate).delete("login:blocked:127.0.0.1");
    }
}
