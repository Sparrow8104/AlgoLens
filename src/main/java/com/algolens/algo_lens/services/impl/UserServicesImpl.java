package com.algolens.algo_lens.services.impl;


import com.algolens.algo_lens.client.CodeforcesApiClient;
import com.algolens.algo_lens.dtos.ContestDTO;
import com.algolens.algo_lens.dtos.user.userInfo.CodeforcesUserDTO;
import com.algolens.algo_lens.dtos.user.userInfo.UserInfoResponseDto;
import com.algolens.algo_lens.dtos.user.userInfo.UserProfileDTO;
import com.algolens.algo_lens.dtos.user.userRating.RatingChangeDTO;
import com.algolens.algo_lens.dtos.user.userRating.UserRatingResponseDTO;
import com.algolens.algo_lens.dtos.user.userStatus.SubmissionDTO;
import com.algolens.algo_lens.dtos.user.userStatus.UserStatusResponseDTO;
import com.algolens.algo_lens.exception.ExternalApiException;
import com.algolens.algo_lens.exception.UserNotFoundException;
import com.algolens.algo_lens.mapper.UserMapper;
import com.algolens.algo_lens.services.UserServices;
import com.algolens.algo_lens.services.stats.UserStatsService;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Comparator;
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
        if(userInfoResponseDto==null||userInfoResponseDto.getResult().isEmpty()) {
            throw new UserNotFoundException(
                    "User not found with handle: " + handle
            );
        }

        CodeforcesUserDTO codeforcesUserDTO = userInfoResponseDto.getResult().getFirst();
        UserStatusResponseDTO response = codeforcesApiClient.getUserSubmissions(handle);

        if(response==null||response.getResult()==null) {
            throw new ExternalApiException("API return null response");
        }
        List<SubmissionDTO> submissionDTOList = response.getResult();

        UserRatingResponseDTO ratingResponse = codeforcesApiClient.getUserRatings(handle);
        if(ratingResponse==null) {
            throw new ExternalApiException("Failed to fetch data from codeforces");
        }

        int problemsSolved =
                userStatsService.calculateProblemsSolved(submissionDTOList);

        int contestsParticipated =
                userStatsService.calculateContestsParticipated(ratingResponse);

        Set<LocalDate> solvedDates =
                userStatsService.getSolvedDates(submissionDTOList);

        int streakDays=
                userStatsService.calculateStreakDays(solvedDates);

        LocalDate lastActiveDate=
                userStatsService.getLastActiveDate(codeforcesUserDTO);

        return userMapper.mapToUserProfileDTO(
                codeforcesUserDTO,
                problemsSolved,
                contestsParticipated,
                streakDays,
                lastActiveDate
        );
    }

    @Override
    public List<ContestDTO> getUserContestHistory(String handle) {
        UserRatingResponseDTO userRatingResponseDTO = codeforcesApiClient.getUserRatings(handle);
        if(userRatingResponseDTO==null||userRatingResponseDTO.getResult().isEmpty()) {
            throw new ExternalApiException("Failed to fetch contest details from codeforces");
        }
        return userRatingResponseDTO.getResult()
                .stream()
                .sorted(Comparator.comparing(RatingChangeDTO::getContestId).reversed())
                .map(userMapper::mapToContestDTO)
                .toList();
    }


}
