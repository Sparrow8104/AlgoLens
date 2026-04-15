package com.algolens.algo_lens.auth.repositories;

import com.algolens.algo_lens.auth.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String username);

    java.util.List<User> findByEmailVerifiedTrueAndNotifyBeforeContestTrue();
}
