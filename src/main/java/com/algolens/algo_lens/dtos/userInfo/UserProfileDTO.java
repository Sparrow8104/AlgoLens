package com.algolens.algo_lens.dtos.userInfo;

import java.time.LocalDate;

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
