package com.algolens.algo_lens.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record ContestDTO(int contestId,
                         String contestName,
                         int rank,
                         int oldRating,
                         int newRating,
                         int ratingChange) {

}
