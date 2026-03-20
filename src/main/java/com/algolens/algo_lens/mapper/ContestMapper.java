package com.algolens.algo_lens.mapper;

import com.algolens.algo_lens.dtos.ContestDTO;
import com.algolens.algo_lens.dtos.contest.CodeforcesContestItemDTO;
import com.algolens.algo_lens.dtos.contest.UpcomingContestDTO;
import org.springframework.stereotype.Component;

@Component
public class ContestMapper {

    public UpcomingContestDTO toDTO(CodeforcesContestItemDTO codeforcesContestItemDTO) {
        return UpcomingContestDTO.builder()
                .contestId(codeforcesContestItemDTO.getId())
                .name(codeforcesContestItemDTO.getName())
                .type(codeforcesContestItemDTO.getType())
                .durationSeconds(codeforcesContestItemDTO.getDurationSeconds())
                .startTimeSeconds(codeforcesContestItemDTO.getStartTimeSeconds())
                .relativeTimeSeconds(codeforcesContestItemDTO.getRelativeTimeSeconds())
                .build();
    }
}
