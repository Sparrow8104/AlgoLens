package com.algolens.algo_lens.dtos.contest;

import lombok.Builder;

@Builder
public record UpcomingContestDTO(
        int contestId,
        String name,
        String type,
        int durationSeconds,
        int startTimeSeconds,
        int relativeTimeSeconds){
}
