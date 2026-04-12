package com.algolens.algo_lens.auth.services;

import com.algolens.algo_lens.auth.repositories.PendingRegistrationRepository;
import com.algolens.algo_lens.auth.repositories.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class TokenCleanupService {

    private static final Logger log = LoggerFactory.getLogger(TokenCleanupService.class);

    private final PendingRegistrationRepository pendingRegistrationRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenCleanupService(PendingRegistrationRepository pendingRegistrationRepository,
                               RefreshTokenRepository refreshTokenRepository) {
        this.pendingRegistrationRepository = pendingRegistrationRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }


    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();

        int deletedPending = pendingRegistrationRepository.deleteAllExpired(now);
        log.info("[Cleanup] Deleted {} expired pending registrations", deletedPending);

        refreshTokenRepository.deleteAllExpired(now);
        log.info("[Cleanup] Deleted expired refresh tokens");
    }
}