package com.algolens.algo_lens.mapper;

import com.algolens.algo_lens.dtos.ContestDTO;
import com.algolens.algo_lens.dtos.user.userInfo.CodeforcesUserDTO;
import com.algolens.algo_lens.dtos.user.userInfo.UserProfileDTO;
import com.algolens.algo_lens.dtos.user.userRating.RatingChangeDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserMapper {
    public UserProfileDTO mapToUserProfileDTO(
           CodeforcesUserDTO userInfo,
            int problemsSolved,
            int contestsParticipated,
            int streakDays,
            LocalDate lastActiveDate
    ){
    return new UserProfileDTO(
            userInfo.getHandle(),
            userInfo.getRating(),
            userInfo.getMaxRating(),
            userInfo.getRank(),
            problemsSolved,
            contestsParticipated,
            streakDays,
            lastActiveDate,
            userInfo.getAvatar()
    );

    }

    public ContestDTO mapToContestDTO(RatingChangeDTO ratingChangeDTO){
        return ContestDTO.builder()
                .contestId(ratingChangeDTO.getContestId())
                .contestName(ratingChangeDTO.getContestName())
                .rank(ratingChangeDTO.getRank())
                .oldRating(ratingChangeDTO.getOldRating())
                .newRating(ratingChangeDTO.getNewRating())
                .ratingChange(ratingChangeDTO.getNewRating() - ratingChangeDTO.getOldRating())
                .build();
    }
}
