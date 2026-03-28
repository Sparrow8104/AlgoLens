package com.algolens.algo_lens.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record RatingGraphDTO(int contestId,
                             String contestName,
                             int rating,
                             LocalDate date) {
}
