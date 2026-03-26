package com.algolens.algo_lens.dtos.code;

public record CodeCompareRequestDTO(
        String handle1,
        String handle2,
        int contestId
) {}