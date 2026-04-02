package com.algolens.algo_lens.dtos.analysis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AiAnalysisRequestDTO(String handle) implements Serializable {
}
