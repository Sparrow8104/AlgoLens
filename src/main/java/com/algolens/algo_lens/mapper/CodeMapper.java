package com.algolens.algo_lens.mapper;


import com.algolens.algo_lens.dtos.code.CommonContestDTO;
import org.springframework.stereotype.Component;

@Component
public class CodeMapper {

    public CommonContestDTO mapToCommonContestDTO(int contestId,
                                                  String contestName) {
        return CommonContestDTO.builder()
                .contestId(contestId)
                .contestName(contestName)
                .build();
    }
}
