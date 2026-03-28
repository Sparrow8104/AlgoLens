package com.algolens.algo_lens.dtos.contest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CodeforcesContestResponseDTO implements Serializable {
    String status;
    List<CodeforcesContestItemDTO> result;
}
