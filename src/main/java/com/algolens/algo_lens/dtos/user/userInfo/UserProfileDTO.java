package com.algolens.algo_lens.dtos.user.userInfo;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserProfileDTO(String handle,
                             Integer rating,
                             Integer maxRating,
                             String rank,
                             int problemsSolved,
                             int contestsParticipated,
                             int streakDays,
                             LocalDate lastActiveDate,
                             String avatar
                             ) {
}
