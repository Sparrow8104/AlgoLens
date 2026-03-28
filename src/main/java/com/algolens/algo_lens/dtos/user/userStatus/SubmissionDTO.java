package com.algolens.algo_lens.dtos.user.userStatus;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class SubmissionDTO implements Serializable {
    private Long id;
    private String verdict;
    private ProblemDTO problem;
    private Long creationTimeSeconds;
    private String programmingLanguage;
    private Long timeConsumedMillis;
    private Long memoryConsumedBytes;
}
