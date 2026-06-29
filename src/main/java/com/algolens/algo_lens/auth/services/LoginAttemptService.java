package com.algolens.algo_lens.auth.services;

import com.algolens.algo_lens.auth.exception.TooManyRequestsException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_DURATION_SECONDS = 900; // 15 minutes

    private static final String LOGIN_ATTEMPTS_PREFIX = "login:attempts:";
    private static final String LOGIN_BLOCKED_PREFIX = "login:blocked:";

    private final StringRedisTemplate redisTemplate;

    public LoginAttemptService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void checkBlocked(String ip) {
        String blockedKey = LOGIN_BLOCKED_PREFIX + ip;
        Boolean isBlocked = redisTemplate.hasKey(blockedKey);
        if (Boolean.TRUE.equals(isBlocked)) {
            throw new TooManyRequestsException(
                    "Too many failed login attempts. Please try again later.");
        }
    }

    public void recordFailure(String ip) {
        String attemptsKey = LOGIN_ATTEMPTS_PREFIX + ip;
        String blockedKey = LOGIN_BLOCKED_PREFIX + ip;

        Long newAttempts = redisTemplate.opsForValue().increment(attemptsKey);
        if (newAttempts != null && newAttempts == 1) {
            redisTemplate.expire(attemptsKey, LOCK_DURATION_SECONDS, TimeUnit.SECONDS);
        }

        if (newAttempts != null && newAttempts >= MAX_ATTEMPTS) {
            redisTemplate.opsForValue().set(blockedKey, "1", LOCK_DURATION_SECONDS, TimeUnit.SECONDS);
            redisTemplate.delete(attemptsKey); // Clear attempts once blocked
        }
    }

    public void clearFailures(String ip) {
        redisTemplate.delete(LOGIN_ATTEMPTS_PREFIX + ip);
        redisTemplate.delete(LOGIN_BLOCKED_PREFIX + ip);
    }
}