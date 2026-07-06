package com.algolens.algo_lens.services.service;

import com.algolens.algo_lens.dtos.contest.CodeforcesContestItemDTO;
import com.algolens.algo_lens.dtos.contest.UpcomingContestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface ContestServices {
    List<CodeforcesContestItemDTO> getAllContestsPaginated();
    List<UpcomingContestDTO> getUpcomingContests();
    Page<UpcomingContestDTO> getAllContestsPaginated(Pageable pageable);
    Page<UpcomingContestDTO> getUpcomingContestsPaginated(Pageable pageable);
}
