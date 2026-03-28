package com.algolens.algo_lens.dtos.code;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import java.util.List;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record DiffDeltaDTO(
        String type,
        int position,
        List<String> originalLines,
        List<String> newLines
) {}