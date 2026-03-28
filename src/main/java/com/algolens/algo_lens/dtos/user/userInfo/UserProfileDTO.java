package com.algolens.algo_lens.dtos.user.userInfo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
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
