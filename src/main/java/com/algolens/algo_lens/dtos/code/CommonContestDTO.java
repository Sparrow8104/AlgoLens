package com.algolens.algo_lens.dtos.code;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record CommonContestDTO(
        int contestId,
        String contestName
) {}