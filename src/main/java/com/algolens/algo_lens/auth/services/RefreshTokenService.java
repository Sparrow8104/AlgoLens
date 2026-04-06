package com.algolens.algo_lens.auth.services;

import com.algolens.algo_lens.auth.entities.RefreshToken;
import com.algolens.algo_lens.auth.entities.User;
import com.algolens.algo_lens.auth.repositories.RefreshTokenRepository;
import com.algolens.algo_lens.auth.repositories.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Ref;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(String username) {
        User user=userRepository.findByEmail(username).orElseThrow(()->new UsernameNotFoundException("User not found"+username));
        RefreshToken refreshToken=user.getRefreshToken();

        if(refreshToken==null){
            long refreshTokenValidity=1000*60*30;
            refreshToken=RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .expirationTime(Instant.now().plusMillis(refreshTokenValidity))
                    .user(user)
                    .build();
        }
        return refreshToken;
    }
    public RefreshToken verifyRefreshToken(String refreshToken) {
        RefreshToken refreshToken1=refreshTokenRepository.findByRefreshToken(refreshToken).orElseThrow(()->
                new RuntimeException("Token not fount"));
                if(refreshToken1.getExpirationTime().isBefore(Instant.now())){
                    throw new RuntimeException("Token expired");
                }
        return refreshToken1;
    }

}
