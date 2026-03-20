package com.algolens.algo_lens.services.service;

import com.algolens.algo_lens.dtos.comparison.RatingComparisonDTO;
import com.algolens.algo_lens.dtos.comparison.SubmissionCompareRequestDTO;
import com.algolens.algo_lens.dtos.comparison.SubmissionCompareResponseDTO;

public interface ComparisonServices {
    RatingComparisonDTO compareRatings(String handle1, String handle2);
    SubmissionCompareResponseDTO findSubmissions(SubmissionCompareRequestDTO submissionCompareRequestDTO);
}
