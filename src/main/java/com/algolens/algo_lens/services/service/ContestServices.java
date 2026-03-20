package com.algolens.algo_lens.services.service;

import com.algolens.algo_lens.dtos.contest.CodeforcesContestItemDTO;
import com.algolens.algo_lens.dtos.contest.UpcomingContestDTO;

import java.util.List;


public interface ContestServices {
    List<CodeforcesContestItemDTO> fetchAllContests();
    List<UpcomingContestDTO> getUpcomingContests();
}
