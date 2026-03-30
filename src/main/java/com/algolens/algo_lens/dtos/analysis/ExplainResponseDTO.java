package com.algolens.algo_lens.dtos.analysis;

import lombok.Builder;

import java.util.List;

@Builder
public record ExplainResponseDTO ( String explanation,
                                   String timeComplexity,
                                   String spaceComplexity,
                                   String patternIdentified,
                                   List<String> personalisedTips){
}
