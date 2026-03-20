package com.algolens.algo_lens.dtos.comparison;

import lombok.Builder;

@Builder
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
