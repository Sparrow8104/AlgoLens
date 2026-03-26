package com.algolens.algo_lens.services.service;

import com.algolens.algo_lens.dtos.code.CodeCompareRequestDTO;
import com.algolens.algo_lens.dtos.code.CodeCompareResponseDTO;
import com.algolens.algo_lens.dtos.code.CommonContestDTO;

import java.util.List;

public interface CodeServices {
    List<CommonContestDTO> getCommonContests(String handle1,String handle2);
    CodeCompareResponseDTO compareCode(CodeCompareRequestDTO request);
}
