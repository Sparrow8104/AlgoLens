package com.algolens.algo_lens.dtos.comparison;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SubmissionCompareRequestDTO(String handle1,
                                          String handle2,
                                          int contestId,
                                          String index) {
}
