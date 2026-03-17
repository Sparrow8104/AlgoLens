package com.algolens.algo_lens.dtos.userRating;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingChangeDTO {
    private Integer contestId;
    private String contestName;
    private String handle;
    private Integer rank;
    private Integer oldRating;
    private Integer newRating;
    private Long ratingUpdateTimeSeconds;
}
