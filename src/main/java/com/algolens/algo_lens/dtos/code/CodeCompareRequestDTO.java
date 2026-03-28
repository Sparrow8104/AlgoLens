package com.algolens.algo_lens.dtos.code;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CodeCompareRequestDTO(
        String handle1,
        String handle2,
        int contestId
) {}