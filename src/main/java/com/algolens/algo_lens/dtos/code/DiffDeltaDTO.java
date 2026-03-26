package com.algolens.algo_lens.dtos.code;

import lombok.Builder;
import java.util.List;

@Builder
public record DiffDeltaDTO(
        String type,
        int position,
        List<String> originalLines,
        List<String> newLines
) {}