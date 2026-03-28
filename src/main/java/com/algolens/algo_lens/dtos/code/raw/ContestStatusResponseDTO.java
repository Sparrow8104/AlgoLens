package com.algolens.algo_lens.dtos.code.raw;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContestStatusResponseDTO implements Serializable {
    private String status;
    private List<ContestSubmissionDTO> result;
}
