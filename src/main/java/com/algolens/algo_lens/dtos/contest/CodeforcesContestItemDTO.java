package com.algolens.algo_lens.dtos.contest;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
public class CodeforcesContestItemDTO implements Serializable {
    private int id;
    private String name;
    private String type;
    private String phase;
    private boolean frozen;
    private int durationSeconds;
    private int startTimeSeconds;
    private int relativeTimeSeconds;
}
