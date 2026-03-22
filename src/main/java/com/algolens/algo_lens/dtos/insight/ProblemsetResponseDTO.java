package com.algolens.algo_lens.dtos.insight;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ProblemsetResponseDTO {
    private String status;
    private ProblemsetResultDTO result;
}
