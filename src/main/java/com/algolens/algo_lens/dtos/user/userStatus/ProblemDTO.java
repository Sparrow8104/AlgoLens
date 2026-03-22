package com.algolens.algo_lens.dtos.user.userStatus;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ProblemDTO implements Serializable {
    private Integer contestId;
    private String index;
    private String name;
    private Integer rating;
    private List<String> tags;
}
