package com.algolens.algo_lens.dtos.userInfo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CodeforcesUserDTO {
    private String handle;
    private Integer rating;
    private Integer maxRating;
    private String rank;
    private String avatar;
    private Long lastOnlineTimeSeconds;

}
