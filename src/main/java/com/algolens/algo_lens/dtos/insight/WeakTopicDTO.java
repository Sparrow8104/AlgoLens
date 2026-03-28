package com.algolens.algo_lens.dtos.insight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record WeakTopicDTO(
        String tag,
        int totalAttempts,
        int solvedCount,
        int unsolvedCount,
        double acRate
) {
}
