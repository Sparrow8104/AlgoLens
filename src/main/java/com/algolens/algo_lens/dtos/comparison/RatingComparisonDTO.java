package com.algolens.algo_lens.dtos.comparison;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record RatingComparisonDTO(
        String handle1,
        String handle2,
        Integer rating1,
        Integer rating2,
        Integer ratingDelta,
        String higherRatedHandle,
        Integer maxRating1,
        Integer maxRating2,
        String rank1,
        String rank2,
        int contestsParticipated1,
        int contestsParticipated2
) {
}
