package com.algolens.algo_lens.dtos.code;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProblemCompareDTO(
        String index,
        SubmissionCodeDTO submission1,
        SubmissionCodeDTO submission2
) {}