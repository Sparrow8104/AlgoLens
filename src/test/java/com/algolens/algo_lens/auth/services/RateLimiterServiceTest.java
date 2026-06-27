package com.algolens.algo_lens.auth.services;

import com.algolens.algo_lens.auth.exception.RateLimitExceededException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class RateLimiterServiceTest {

    private RateLimiterService rateLimiterService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rateLimiterService = new RateLimiterService(redisTemplate);
    }

    @Test
    void checkAndRecordPhone_Success() {
        // Regular Case: Redis script returns 0 (success)
        when(redisTemplate.execute(
                any(DefaultRedisScript.class),
                eq(List.of("phone:cd:+1234567890", "phone:hr:+1234567890", "phone:cd:ip:127.0.0.1", "phone:hr:ip:127.0.0.1")),
                eq("60"), eq("5"), eq("3600"), eq("60"), eq("5"), eq("3600")
        )).thenReturn(List.of(0L, 0L));

        assertDoesNotThrow(() ->
                rateLimiterService.checkAndRecordPhone("+1234567890", "127.0.0.1")
        );
    }

    @Test
    void checkAndRecordPhone_CooldownHit() {
        // Edge Case: Identity Cooldown Hit (e.g. user requests SMS again in <60 seconds)
        when(redisTemplate.execute(
                any(DefaultRedisScript.class),
                anyList(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn(List.of(1L, 45L));

        RateLimitExceededException exception = assertThrows(RateLimitExceededException.class, () ->
                rateLimiterService.checkAndRecordPhone("+1234567890", "127.0.0.1")
        );

        assertTrue(exception.getMessage().contains("Please wait 45s"));
        assertEquals(45L, exception.getRetryAfterSeconds());
    }

    @Test
    void checkAndRecordPhone_IpCooldownHit() {
        // Edge Case: IP Cooldown Hit (e.g. attacker spamming multiple numbers from the same IP)
        when(redisTemplate.execute(
                any(DefaultRedisScript.class),
                anyList(),
                anyString(), anyString(), anyString(), anyString(), anyString(), anyString()
        )).thenReturn(List.of(3L, 50L));

        RateLimitExceededException exception = assertThrows(RateLimitExceededException.class, () ->
                rateLimiterService.checkAndRecordPhone("+1234567890", "127.0.0.1")
        );

        assertTrue(exception.getMessage().contains("another verification from this IP"));
        assertEquals(50L, exception.getRetryAfterSeconds());
    }
}
