package com.algolens.algo_lens.dtos.insight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProblemsetResponseDTO implements Serializable {
    private String status;
    private ProblemsetResultDTO result;
}
