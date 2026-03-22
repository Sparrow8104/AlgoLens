package com.algolens.algo_lens.dtos.insight;

import lombok.Builder;

@Builder
public record WeakTopicDTO(
        String tag,
        int totalAttempts,
        int solvedCount,
        int unsolvedCount,
        double acRate
) {
}
