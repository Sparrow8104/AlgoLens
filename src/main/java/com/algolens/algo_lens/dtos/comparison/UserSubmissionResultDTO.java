package com.algolens.algo_lens.dtos.comparison;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserSubmissionResultDTO(
        String handle,
        boolean solved,
        String verdict,
        String programmingLanguage,
        Long timeConsumedMillis,
        Long memoryConsumedBytes,
        Long submittedAt,
        Long submissionId
) {
}
