package com.algolens.algo_lens.auth.repositories;

import com.algolens.algo_lens.auth.entities.RefreshToken;
import com.algolens.algo_lens.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByRefreshToken(String token);

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken rt WHERE rt.user=:user")
    void deleteByUser(User user);
}
