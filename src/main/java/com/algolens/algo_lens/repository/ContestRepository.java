package com.algolens.algo_lens.repository;

import com.algolens.algo_lens.models.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContestRepository extends JpaRepository<Contest, Long> {
    Optional<Contest> findByCodeforcesId(Integer codeforcesId);

    // Find all contests starting exactly within a specific second range
    List<Contest> findByStartTimeSecondsBetween(Long startInclusive, Long endInclusive);
}
