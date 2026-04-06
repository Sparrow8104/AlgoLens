package com.algolens.algo_lens.auth.repositories;

import com.algolens.algo_lens.auth.entities.RefreshToken;
import com.algolens.algo_lens.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByRefreshToken(String token);

    void deleteByUser(User user);
}
