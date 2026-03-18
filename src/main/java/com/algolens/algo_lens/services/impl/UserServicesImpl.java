package com.algolens.algo_lens.services.impl;


import com.algolens.algo_lens.client.CodeforcesApiClient;
import com.algolens.algo_lens.dtos.ContestDTO;
import com.algolens.algo_lens.dtos.userInfo.CodeforcesUserDTO;
import com.algolens.algo_lens.dtos.userInfo.UserInfoResponseDto;
import com.algolens.algo_lens.dtos.userInfo.UserProfileDTO;
import com.algolens.algo_lens.dtos.userRating.RatingChangeDTO;
import com.algolens.algo_lens.dtos.userRating.UserRatingResponseDTO;
import com.algolens.algo_lens.dtos.userStatus.ProblemDTO;
import com.algolens.algo_lens.dtos.userStatus.SubmissionDTO;
import com.algolens.algo_lens.dtos.userStatus.UserStatusResponseDTO;
import com.algolens.algo_lens.mapper.UserMapper;
import com.algolens.algo_lens.services.UserServices;
import com.algolens.algo_lens.services.stats.UserStatsService;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServicesImpl implements UserServices {

    private final CodeforcesApiClient codeforcesApiClient;
    private final UserMapper userMapper;
    private final UserStatsService userStatsService;

    public UserServicesImpl(CodeforcesApiClient codeforcesApiClient, UserMapper userMapper, UserStatsService userStatsService) {
        this.codeforcesApiClient = codeforcesApiClient;
        this.userMapper = userMapper;
        this.userStatsService = userStatsService;
    }

    @Override
    public UserProfileDTO getUserProfile(String handle) {

        UserInfoResponseDto userInfoResponseDto = codeforcesApiClient.getUserInfo(handle);
        if(userInfoResponseDto.getResult().isEmpty()) {
            return null;
        }
        CodeforcesUserDTO codeforcesUserDTO = userInfoResponseDto.getResult().getFirst();
        UserStatusResponseDTO response = codeforcesApiClient.getUserSubmissions(handle);
        List<SubmissionDTO> submissionDTOList = response.getResult();

        UserRatingResponseDTO ratingResponse = codeforcesApiClient.getUserRatings(handle);

        int problemsSolved = userStatsService.calculateProblemsSolved(submissionDTOList);
        int contestsParticipated = userStatsService.calculateContestsParticipated(ratingResponse);
        int streakDays=userStatsService.calculateStreakDays(userStatsService.getSolvedDates(submissionDTOList));
        LocalDate lastActiveDate=userStatsService.getLastActiveDate(codeforcesUserDTO);
        return userMapper.mapToUserProfileDTO(
                codeforcesUserDTO,
                problemsSolved,
                contestsParticipated,
                streakDays,
                lastActiveDate
        );
    }




}
