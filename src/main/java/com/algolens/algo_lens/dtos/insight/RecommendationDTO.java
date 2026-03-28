package com.algolens.algo_lens.dtos.insight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record RecommendationDTO(
        Integer contestId,
        String index,
        String name,
        Integer rating,
        List<String> tags
) {
}
