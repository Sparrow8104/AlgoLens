package com.algolens.algo_lens.dtos.contest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record UpcomingContestDTO(
        int contestId,
        String name,
        String type,
        int durationSeconds,
        int startTimeSeconds,
        int relativeTimeSeconds){
}
