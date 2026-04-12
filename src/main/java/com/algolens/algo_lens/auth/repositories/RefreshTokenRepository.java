package com.algolens.algo_lens.auth.repositories;

import com.algolens.algo_lens.auth.entities.RefreshToken;
import com.algolens.algo_lens.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String hashedToken);

    List<RefreshToken> findAllByUser(User user);

    void deleteByTokenId(Long tokenId);

    void deleteByUser(User user);

    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.expirationTime < :now")
    void deleteAllExpired(@Param("now") Instant now);

    Optional<RefreshToken> findByUserAndDeviceId(User user, String deviceId);
}
