package com.algolens.algo_lens.dtos.insight;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
public class ProblemsetResponseDTO implements Serializable {
    private String status;
    private ProblemsetResultDTO result;
}
