package com.algolens.algo_lens.dtos.friend;

import lombok.Builder;

@Builder
public record LeaderboardEntryDTO(
        int rank,
        String handle,
        Integer rating,
        String tier,
        Integer maxRating
) {
}
