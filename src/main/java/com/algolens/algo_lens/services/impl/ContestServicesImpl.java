package com.algolens.algo_lens.services.impl;

import com.algolens.algo_lens.client.CodeforcesApiClient;
import com.algolens.algo_lens.dtos.contest.CodeforcesContestItemDTO;
import com.algolens.algo_lens.dtos.contest.UpcomingContestDTO;
import com.algolens.algo_lens.mapper.ContestMapper;
import com.algolens.algo_lens.services.service.ContestServices;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ContestServicesImpl implements ContestServices {

    private final CodeforcesApiClient codeforcesApiClient;
    private final ContestMapper contestMapper;

    public ContestServicesImpl(CodeforcesApiClient codeforcesApiClient, ContestMapper contestMapper) {
        this.codeforcesApiClient = codeforcesApiClient;
        this.contestMapper = contestMapper;
    }

    @Override
    public List<CodeforcesContestItemDTO> fetchAllContests() {
        return codeforcesApiClient.getContests().getResult();
    }

    @Override
    public List<UpcomingContestDTO> getUpcomingContests() {
        return fetchAllContests().stream()
                .filter(c->"BEFORE".equals(c.getPhase()))
                .sorted(Comparator.comparingLong(CodeforcesContestItemDTO::getStartTimeSeconds))
                .map(contestMapper::toDTO)
                .toList();
    }


}
