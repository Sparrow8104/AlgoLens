package com.algolens.algo_lens.dtos.analysis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProblemAnalysisDTO(
        int contestId,
        String problemIndex,
        String problemName,
        String likelyIssue,
        String conceptToStudy,
        String actionableTip
) implements Serializable {}