package com.algolens.algo_lens.dtos.analysis;

public record ExplainRequestDTO(String handle,
                                String code,
                                String language,
                                Integer contestId,
                                String problemIndex) {

}
