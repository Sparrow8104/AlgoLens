package com.algolens.algo_lens.dtos.comparison;

import lombok.Builder;

@Builder
public record SubmissionCompareResponseDTO(
        int contestId,
        String index,
        UserSubmissionResultDTO user1Result,
        UserSubmissionResultDTO user2Result

) {
}
