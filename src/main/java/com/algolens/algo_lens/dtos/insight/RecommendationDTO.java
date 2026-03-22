package com.algolens.algo_lens.dtos.insight;

import lombok.Builder;

import java.util.List;

@Builder
public record RecommendationDTO(
        Integer contestId,
        String index,
        String name,
        Integer rating,
        List<String> tags
) {
}
