package com.algolens.algo_lens.dtos.insight;

import lombok.Builder;

import java.util.List;

@Builder
public record UpsolveDTO<url>(
        Integer contestId,
        String index,
        String name,
        Integer rating,
        List<String> tags,
        String bestVerdict,
        String url
) {
}
