package com.algolens.algo_lens.dtos.contest;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeforcesContestItemDTO{
    private int id;
    private String name;
    private String type;
    private String phase;
    private boolean frozen;
    private int durationSeconds;
    private int startTimeSeconds;
    private int relativeTimeSeconds;
}
