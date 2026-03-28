package com.algolens.algo_lens.dtos.comparison;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record SubmissionCompareResponseDTO(
        int contestId,
        String index,
        UserSubmissionResultDTO user1Result,
        UserSubmissionResultDTO user2Result

) {
}
