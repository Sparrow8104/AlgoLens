package com.algolens.algo_lens.dtos.code;

import lombok.Builder;
import java.util.List;

@Builder
public record CodeCompareResponseDTO(
        int contestId,
        String contestName,
        List<ProblemCompareDTO> problems
) {}