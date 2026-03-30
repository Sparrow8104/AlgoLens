package com.algolens.algo_lens.dtos.analysis;

import lombok.Builder;

import java.util.List;

@Builder
public record UpsolveAnalysisResponseDTO(String whatWentWrong,
                                         String correctApproachHint,
                                         List<String> topicsToStudy,
                                         String encouragement) {
}
