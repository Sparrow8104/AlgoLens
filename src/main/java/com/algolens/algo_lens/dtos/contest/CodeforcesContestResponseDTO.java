package com.algolens.algo_lens.dtos.contest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class CodeforcesContestResponseDTO implements Serializable {
    String status;
    List<CodeforcesContestItemDTO> result;
}
