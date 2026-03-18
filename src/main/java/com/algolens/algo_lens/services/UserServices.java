package com.algolens.algo_lens.services;

import com.algolens.algo_lens.dtos.ContestDTO;
import com.algolens.algo_lens.dtos.RatingGraphDTO;
import com.algolens.algo_lens.dtos.SubmissionStatsDTO;
import com.algolens.algo_lens.dtos.user.userInfo.UserProfileDTO;

import java.util.List;

public interface UserServices {

    UserProfileDTO getUserProfile(String handle);

    List<ContestDTO> getUserContestHistory(String handle);

    List<RatingGraphDTO> getUserRatingGraph(String handle);

    SubmissionStatsDTO getSubmissionStats(String handle);
}
