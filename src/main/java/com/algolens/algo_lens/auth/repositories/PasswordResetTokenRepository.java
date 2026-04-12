package com.algolens.algo_lens.auth.repositories;

import com.algolens.algo_lens.auth.entities.PasswordResetToken;
import com.algolens.algo_lens.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByJti(String jti);

    Optional<PasswordResetToken> findByUser(User user);
}