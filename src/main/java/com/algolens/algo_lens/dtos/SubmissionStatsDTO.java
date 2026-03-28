package com.algolens.algo_lens.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.util.Map;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record SubmissionStatsDTO(int totalSubmissions,
                                 int solvedProblems,
                                 int unSolvedProblems,
                                 Map<String,Integer> verdictsCount) {
}
