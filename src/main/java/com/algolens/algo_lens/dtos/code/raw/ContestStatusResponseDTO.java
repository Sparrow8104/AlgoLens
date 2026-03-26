package com.algolens.algo_lens.dtos.code.raw;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ContestStatusResponseDTO implements Serializable {
    private String status;
    private List<ContestSubmissionDTO> result;
}
