package com.algolens.algo_lens.dtos.userStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmissionDTO {
    private String verdict;
    private ProblemDTO problem;
    private Long creationTimeSeconds;
}
