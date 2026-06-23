package com.algolens.algo_lens.auth.services;

import com.algolens.algo_lens.auth.entities.RefreshToken;
import com.algolens.algo_lens.auth.entities.User;
import com.algolens.algo_lens.auth.exception.InvalidTokenException;
import com.algolens.algo_lens.auth.exception.TokenExpiredException;
import com.algolens.algo_lens.auth.exception.TokenRefreshException;
import com.algolens.algo_lens.auth.repositories.RefreshTokenRepository;
import com.algolens.algo_lens.auth.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-expiration:604800}")
    private long refreshTokenExpiration;

    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public String createRefreshToken(User user,String deviceId,String userAgent,String ip) {

       Optional<RefreshToken> existing=refreshTokenRepository.findByUserAndDeviceId(user, deviceId);
        if (existing.isPresent()) {
            return rotateRefreshToken(existing.get());
        }

        String rawToken = UUID.randomUUID().toString();
        String hashedToken = hashToken(rawToken);

        RefreshToken token = RefreshToken.builder()
                .refreshToken(hashedToken)
                .user(user)
                .expirationTime(Instant.now().plusSeconds(refreshTokenExpiration))
                .deviceId(deviceId)
                .userAgent(userAgent)
                .ipAddress(ip)
                .createdAt(Instant.now())
                .used(false)
                .build();

        refreshTokenRepository.saveAndFlush(token);
        return rawToken;

    }
    @Transactional
    public RefreshToken verifyRefreshToken(String rawToken) {
        String hashedToken = hashToken(rawToken);

        RefreshToken token = refreshTokenRepository.findByRefreshToken(hashedToken)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token."));

        if (token.isUsed()) {
            refreshTokenRepository.deleteByUser(token.getUser());
            throw new InvalidTokenException(
                    "Refresh token reuse detected. All sessions have been revoked for security.");
        }

        if (token.getExpirationTime().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenExpiredException("Refresh token has expired. Please log in again.");
        }

        return token;
    }
    @Transactional
    public String rotateRefreshToken(RefreshToken oldToken) {
        oldToken.setUsed(true);
        refreshTokenRepository.save(oldToken);
        refreshTokenRepository.flush();

        String rawToken = UUID.randomUUID().toString();
        String hashedToken = hashToken(rawToken);

        RefreshToken newToken = RefreshToken.builder()
                .refreshToken(hashedToken)
                .user(oldToken.getUser())
                .expirationTime(Instant.now().plusSeconds(refreshTokenExpiration))
                .deviceId(oldToken.getDeviceId())
                .userAgent(oldToken.getUserAgent())
                .ipAddress(oldToken.getIpAddress())
                .createdAt(Instant.now())
                .used(false)
                .build();

        refreshTokenRepository.saveAndFlush(newToken);
        return rawToken;
    }

    public List<RefreshToken> getActiveSessions(User user) {
        return refreshTokenRepository.findAllByUser(user);
    }

    @Transactional
    public void revokeSession(Long tokenId, User user) {
        RefreshToken token = refreshTokenRepository.findById(tokenId)
                .orElseThrow(() -> new InvalidTokenException("Session not found."));

        // Security: ensure the token belongs to this user
        if (!token.getUser().getUserId().equals(user.getUserId())) {
            throw new InvalidTokenException("Session not found.");
        }

        refreshTokenRepository.deleteByTokenId(tokenId);
    }

    @Transactional
    public void revokeAllSessions(User user) {
        refreshTokenRepository.deleteByUser(user);
    }


    public String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

}
