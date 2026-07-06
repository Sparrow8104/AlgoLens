package com.algolens.algo_lens.services.impl;


import com.algolens.algo_lens.client.CodeforcesApiClient;
import com.algolens.algo_lens.dtos.ContestDTO;
import com.algolens.algo_lens.dtos.RatingGraphDTO;
import com.algolens.algo_lens.dtos.SubmissionStatsDTO;
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
import com.algolens.algo_lens.services.service.UserServices;
import com.algolens.algo_lens.services.stats.UserStatsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

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

    @Override
    public List<RatingGraphDTO> getUserRatingGraph(String handle) {
        UserRatingResponseDTO userRatingResponseDTO = codeforcesApiClient.getUserRatings(handle);
        if(userRatingResponseDTO==null||userRatingResponseDTO.getResult().isEmpty()) {
            throw new ExternalApiException("Failed to fetch rating graph from codeforces");
        }
        return userRatingResponseDTO.getResult()
                .stream()
                .sorted(Comparator.comparing(
                        RatingChangeDTO::getRatingUpdateTimeSeconds))
                .map(userMapper::mapToRatingGraphDTO)
                .toList();
    }

    @Override
    public SubmissionStatsDTO getSubmissionStats(String handle) {
        UserStatusResponseDTO userStatusResponseDTO=codeforcesApiClient.getUserSubmissions(handle);
        if(userStatusResponseDTO==null||userStatusResponseDTO.getResult()==null) {
            throw new ExternalApiException("Failed to fetch submission stats from codeforces");
        }
        List<SubmissionDTO> submissionDTOList = userStatusResponseDTO.getResult();
        Set<String> solvedProblemSet=new HashSet<>();
        Map<String,Integer> verdictsCounts=new HashMap<>();
        for(SubmissionDTO submissionDTO:submissionDTOList) {
            verdictsCounts.merge(submissionDTO.getVerdict(),1,Integer::sum);
            if("Ok".equals(submissionDTO.getVerdict())) {
                String problemKey=submissionDTO.getProblem().getContestId()+"-"+submissionDTO.getProblem().getIndex();
                solvedProblemSet.add(problemKey);
            }
        }
        int totalSubmissions=submissionDTOList.size();
        int solvedProblems=solvedProblemSet.size();
        int unsolvedProblems=totalSubmissions-solvedProblems;

        return SubmissionStatsDTO
                .builder()
                .totalSubmissions(totalSubmissions)
                .solvedProblems(solvedProblems)
                .unSolvedProblems(unsolvedProblems)
                .verdictsCount(verdictsCounts)
                .build();
    }

    @Override
    public Page<ContestDTO> getUserContestHistoryPaginated(String handle, Pageable pageable) {
        List<ContestDTO> contestHistory = getUserContestHistory(handle);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), contestHistory.size());
        List<ContestDTO> slice = start >= end ? List.of() : contestHistory.subList(start, end);
        return new PageImpl<>(slice, pageable, contestHistory.size());
    }
}
