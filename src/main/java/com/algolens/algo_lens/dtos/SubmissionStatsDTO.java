package com.algolens.algo_lens.dtos;

import lombok.Builder;

import java.util.Map;

@Builder
public record SubmissionStatsDTO(int totalSubmissions,
                                 int solvedProblems,
                                 int unSolvedProblems,
                                 Map<String,Integer> verdictsCount) {
}
