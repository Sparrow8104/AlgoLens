package com.algolens.algo_lens.services.service;

import com.algolens.algo_lens.dtos.comparison.RatingComparisonDTO;

public interface ComparisonServices {
    RatingComparisonDTO compareRatings(String handle1, String handle2);
}
