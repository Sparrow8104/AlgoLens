package com.algolens.algo_lens.auth.services;

import com.algolens.algo_lens.auth.entities.RefreshToken;
import com.algolens.algo_lens.auth.entities.User;
import com.algolens.algo_lens.auth.exception.TokenRefreshException;
import com.algolens.algo_lens.auth.repositories.RefreshTokenRepository;
import com.algolens.algo_lens.auth.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public RefreshToken createRefreshToken(String username) {
        User user=userRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("User not found"+username));
        RefreshToken existingToken=user.getRefreshToken();

            refreshTokenRepository.deleteByUser(user);

            existingToken=RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expirationTime(Instant.now().plusMillis(refreshTokenExpiration))
                    .user(user)
                    .build();
        return refreshTokenRepository.save(existingToken);
    }
    public RefreshToken verifyRefreshToken(String refreshToken) {
        RefreshToken refreshToken1=refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(()->
                new TokenRefreshException("Refresh token not fount"));
                if(refreshToken1.getExpirationTime().isBefore(Instant.now())){
                    refreshTokenRepository.delete(refreshToken1);
                    throw new TokenRefreshException("Refresh token has expired.Please log in again");
                }
        return refreshToken1;
    }

    @Transactional
    public RefreshToken rotateRefreshToken(RefreshToken oldToken) {
        refreshTokenRepository.delete(oldToken);
        return createRefreshToken(oldToken.getUser().getEmail());
    }

    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

}
