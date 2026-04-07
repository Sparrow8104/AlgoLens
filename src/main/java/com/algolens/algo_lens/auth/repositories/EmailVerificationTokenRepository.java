package com.algolens.algo_lens.auth.repositories;

import com.algolens.algo_lens.auth.entities.EmailVerificationToken;
import com.algolens.algo_lens.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByToken(String token);
    Optional<EmailVerificationToken> findByUser(User user);
    void deleteByUser(User user);
}
