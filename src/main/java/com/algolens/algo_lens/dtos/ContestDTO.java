package com.algolens.algo_lens.dtos;

import lombok.Builder;

@Builder
public record ContestDTO(int contestId,
                         String contestName,
                         int rank,
                         int oldRating,
                         int newRating,
                         int ratingChange) {

}
