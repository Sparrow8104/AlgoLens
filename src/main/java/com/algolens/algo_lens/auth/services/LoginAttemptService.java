package com.algolens.algo_lens.auth.services;

import com.algolens.algo_lens.auth.exception.TooManyRequestsException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_DURATION_SECONDS = 900;

    private record AttemptRecord(int count, Instant lockedUntil) {}

    private final Map<String, AttemptRecord> attempts = new ConcurrentHashMap<>();


    public void checkBlocked(String ip) {
        AttemptRecord record = attempts.get(ip);
        if (record == null) return;

        if (record.lockedUntil() != null && Instant.now().isBefore(record.lockedUntil())) {
            throw new TooManyRequestsException(
                    "Too many failed login attempts. Please try again later.");
        }

        if (record.lockedUntil() != null) {
            attempts.remove(ip);
        }
    }

    public void recordFailure(String ip) {
        AttemptRecord current = attempts.getOrDefault(ip, new AttemptRecord(0, null));
        int newCount = current.count() + 1;

        Instant lockUntil = (newCount >= MAX_ATTEMPTS)
                ? Instant.now().plusSeconds(LOCK_DURATION_SECONDS)
                : null;

        attempts.put(ip, new AttemptRecord(newCount, lockUntil));
    }


    public void clearFailures(String ip) {
        attempts.remove(ip);
    }
}