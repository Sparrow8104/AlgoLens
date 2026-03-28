package com.algolens.algo_lens.dtos.code;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
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