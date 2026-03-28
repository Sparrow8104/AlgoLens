package com.algolens.algo_lens.dtos.user.userRating;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RatingChangeDTO implements Serializable {
    private Integer contestId;
    private String contestName;
    private String handle;
    private Integer rank;
    private Integer oldRating;
    private Integer newRating;
    private Long ratingUpdateTimeSeconds;
}
