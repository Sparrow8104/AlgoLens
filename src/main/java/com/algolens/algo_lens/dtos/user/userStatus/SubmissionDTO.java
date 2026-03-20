package com.algolens.algo_lens.dtos.user.userStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmissionDTO {
    private Long id;
    private String verdict;
    private ProblemDTO problem;
    private Long creationTimeSeconds;
    private String programmingLanguage;
    private Long timeConsumedMillis;
    private Long memoryConsumedBytes;
}
