package com.algolens.algo_lens.dtos.friend;

import lombok.Builder;

@Builder
public record StreakCompareDTO(
        String handle,
        int currentStreak,
        String lastSubmissionDate
) {
}
