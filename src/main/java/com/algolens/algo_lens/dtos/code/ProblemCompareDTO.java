package com.algolens.algo_lens.dtos.code;

import lombok.Builder;

@Builder
public record ProblemCompareDTO(
        String index,
        SubmissionCodeDTO submission1,
        SubmissionCodeDTO submission2
) {}