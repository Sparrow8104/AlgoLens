package com.algolens.algo_lens.dtos.code.raw;

import com.algolens.algo_lens.dtos.user.userStatus.ProblemDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ContestSubmissionDTO implements Serializable {
    private Long id;
    private AuthorDTO author;
    private String verdict;
    private ProblemDTO problem;
    private Long timeConsumedMillis;
    private Long memoryConsumedBytes;
    private String programmingLanguage;

    public String getHandle() {
        if (author == null
                || author.getMembers() == null
                || author.getMembers().isEmpty()) {
            return null;
        }
        return author.getMembers().getFirst().getHandle();
    }
}
