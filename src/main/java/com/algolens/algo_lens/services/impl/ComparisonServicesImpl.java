package com.algolens.algo_lens.services.impl;

import com.algolens.algo_lens.client.CodeforcesApiClient;
import com.algolens.algo_lens.dtos.comparison.RatingComparisonDTO;
import com.algolens.algo_lens.dtos.user.userInfo.CodeforcesUserDTO;
import com.algolens.algo_lens.mapper.ComparisonMapper;
import com.algolens.algo_lens.services.service.ComparisonServices;
import org.springframework.stereotype.Service;

@Service
public class ComparisonServicesImpl implements ComparisonServices {

    private final CodeforcesApiClient codeforcesApiClient;
    private final ComparisonMapper comparisonMapper;

    public ComparisonServicesImpl(CodeforcesApiClient codeforcesApiClient, ComparisonMapper comparisonMapper) {
        this.codeforcesApiClient = codeforcesApiClient;
        this.comparisonMapper = comparisonMapper;
    }

    @Override
    public RatingComparisonDTO compareRatings(String handle1, String handle2) {
        CodeforcesUserDTO user1=codeforcesApiClient.getUserInfo(handle1).getResult().getFirst();
        CodeforcesUserDTO user2=codeforcesApiClient.getUserInfo(handle2).getResult().getFirst();
        int contestsParticipated1=codeforcesApiClient.getUserRatings(handle1).getResult().size();
        int contestsParticipated2=codeforcesApiClient.getUserRatings(handle2).getResult().size();
        return comparisonMapper.mapToRatingComparisonDTO(
                user1, user2,
                contestsParticipated1,
                contestsParticipated2
        );
    }
}
