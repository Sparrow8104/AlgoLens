package com.algolens.algo_lens.dtos.analysis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AiAnalysisResponseDTO(
        List<ProblemAnalysisDTO> problemAnalyses,
        String overallRecommendation
) implements Serializable {
}
