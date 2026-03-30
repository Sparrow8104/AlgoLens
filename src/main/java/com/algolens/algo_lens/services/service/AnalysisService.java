package com.algolens.algo_lens.services.service;

import com.algolens.algo_lens.dtos.analysis.ExplainRequestDTO;
import com.algolens.algo_lens.dtos.analysis.ExplainResponseDTO;

public interface AnalysisService {
    ExplainResponseDTO explainCode(ExplainRequestDTO request);
}
