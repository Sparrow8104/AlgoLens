package com.algolens.algo_lens.dtos.contest;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CodeforcesContestResponseDTO{
    String status;
    List<CodeforcesContestItemDTO> result;
}
