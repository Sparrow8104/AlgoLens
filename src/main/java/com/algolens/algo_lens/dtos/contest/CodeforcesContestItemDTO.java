package com.algolens.algo_lens.dtos.contest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class CodeforcesContestItemDTO implements Serializable {
    private int id;
    private String name;
    private String type;
    private String phase;
    private boolean frozen;
    private int durationSeconds;
    private long startTimeSeconds;
    private int relativeTimeSeconds;
}
