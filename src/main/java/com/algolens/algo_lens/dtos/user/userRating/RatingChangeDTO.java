package com.algolens.algo_lens.dtos.user.userRating;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
public class RatingChangeDTO implements Serializable {
    private Integer contestId;
    private String contestName;
    private String handle;
    private Integer rank;
    private Integer oldRating;
    private Integer newRating;
    private Long ratingUpdateTimeSeconds;
}
