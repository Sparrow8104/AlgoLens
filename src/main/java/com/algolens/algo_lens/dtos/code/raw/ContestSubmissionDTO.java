package com.algolens.algo_lens.dtos.code.raw;

import com.algolens.algo_lens.dtos.user.userStatus.ProblemDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ContestSubmissionDTO implements Serializable {
    private Long id;
    private Integer contestId;
    private AuthorDTO author;
    private String verdict;
    private ProblemDTO problem;
    private Long timeConsumedMillis;
    private Long memoryConsumedBytes;
    private String programmingLanguage;
    private Long creationTimeSeconds;
    private Long relativeTimeSeconds;

    public String getHandle() {
        if (author == null
                || author.getMembers() == null
                || author.getMembers().isEmpty()) {
            return null;
        }
        return author.getMembers().getFirst().getHandle();
    }
}
