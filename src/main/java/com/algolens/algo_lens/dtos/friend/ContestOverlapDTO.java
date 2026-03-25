package com.algolens.algo_lens.dtos.friend;

import lombok.Builder;

@Builder
public record ContestOverlapDTO(
        String handle,
        int rank,
        int oldRating,
        int newRating,
        int ratingChange
) {
}
