package com.algolens.algo_lens.dtos;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record RatingGraphDTO(int contestId,
                             String contestName,
                             int rating,
                             LocalDate date) {
}
