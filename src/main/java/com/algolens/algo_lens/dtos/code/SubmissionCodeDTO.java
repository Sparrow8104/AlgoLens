package com.algolens.algo_lens.dtos.code;

import lombok.Builder;
import java.util.List;

@Builder
public record SubmissionCodeDTO(
        String handle,
        String problemIndex,
        String language,
        String code,
        String verdict,
        Long timeConsumedMillis,
        Long memoryConsumedBytes,
        Long submissionId,
        List<DiffDeltaDTO> diff
) {}