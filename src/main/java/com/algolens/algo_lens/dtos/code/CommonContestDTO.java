package com.algolens.algo_lens.dtos.code;

import lombok.Builder;

@Builder
public record CommonContestDTO(
        int contestId,
        String contestName
) {}