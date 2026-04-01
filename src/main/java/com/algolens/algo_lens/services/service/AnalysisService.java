package com.algolens.algo_lens.services.service;

import com.algolens.algo_lens.dtos.analysis.AiAnalysisResponseDTO;

public interface AnalysisService {
    AiAnalysisResponseDTO analyzeUpsolve(String handle);
}
