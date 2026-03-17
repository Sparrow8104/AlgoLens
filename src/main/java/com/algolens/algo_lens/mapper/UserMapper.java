package com.algolens.algo_lens.mapper;

import com.algolens.algo_lens.dtos.userInfo.CodeforcesUserDTO;
import com.algolens.algo_lens.dtos.userInfo.UserProfileDTO;

import java.time.LocalDate;

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
}
