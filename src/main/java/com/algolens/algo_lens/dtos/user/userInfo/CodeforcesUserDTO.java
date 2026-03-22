package com.algolens.algo_lens.dtos.user.userInfo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Setter
@Getter
public class CodeforcesUserDTO implements Serializable {
    private String handle;
    private Integer rating;
    private Integer maxRating;
    private String rank;
    private String avatar;
    private Long lastOnlineTimeSeconds;

}
