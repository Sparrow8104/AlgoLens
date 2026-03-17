package com.algolens.algo_lens.services.impl;


import com.algolens.algo_lens.client.CodeforcesApiClient;
import com.algolens.algo_lens.dtos.ContestDTO;
import com.algolens.algo_lens.dtos.userInfo.UserProfileDTO;
import com.algolens.algo_lens.dtos.userRating.RatingChangeDTO;
import com.algolens.algo_lens.dtos.userRating.UserRatingResponseDTO;
import com.algolens.algo_lens.dtos.userStatus.ProblemDTO;
import com.algolens.algo_lens.dtos.userStatus.SubmissionDTO;
import com.algolens.algo_lens.dtos.userStatus.UserStatusResponseDTO;
import com.algolens.algo_lens.mapper.UserMapper;
import com.algolens.algo_lens.services.UserServices;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServicesImpl implements UserServices {

    private final CodeforcesApiClient codeforcesApiClient;
    private final UserMapper userMapper;

    public UserServicesImpl(CodeforcesApiClient codeforcesApiClient, UserMapper userMapper) {
        this.codeforcesApiClient = codeforcesApiClient;
        this.userMapper = userMapper;
    }

    @Override
    public UserProfileDTO getUserProfile(String handle) {
        int problemsSolved = calculateProblemsSolved(handle);
        int contestsParticipated = calculateContestsParticipated(handle);
        return null;
    }

    @Override
    public Page<ContestDTO> getUserContestHistory(String handle, Pageable pageable) {
        return null;
    }

    public int calculateProblemsSolved(String handle) {
        UserStatusResponseDTO response=codeforcesApiClient.getUserSubmissions(handle);
        List<SubmissionDTO> submissions=response.getResult();

        Set<String> solvedProblems =new HashSet<>();

        for(SubmissionDTO submissionDTO:submissions){
            if("OK".equals(submissionDTO.getVerdict())){
                ProblemDTO problem=submissionDTO.getProblem();
                String key=problem.getContestId()+"-"+problem.getIndex();
                solvedProblems.add(key);
            }
        }
        return solvedProblems.size();
    }

    public int calculateContestsParticipated(String handle) {
        UserRatingResponseDTO response=codeforcesApiClient.getUserRatings(handle);
        List<RatingChangeDTO> contestsParticipated=response.getResult();
        return contestsParticipated.size();
    }

    public int calculateStreakDays(String handle) {
        UserStatusResponseDTO response=codeforcesApiClient.getUserSubmissions(handle);
        List<SubmissionDTO> submissions=response.getResult();

        List<SubmissionDTO> solvedSubmissions=submissions.stream()
                .filter(s->"OK".equals(s.getVerdict()))
                .toList();


    }
}
