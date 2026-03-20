package com.algolens.algo_lens.dtos.user.userStatus;


import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProblemDTO {
    private Integer contestId;
    private String index;
    private String name;
    private Integer rating;
    private List<String> tags;
}
