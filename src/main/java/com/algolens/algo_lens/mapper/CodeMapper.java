package com.algolens.algo_lens.mapper;


import com.algolens.algo_lens.dtos.code.CommonContestDTO;
import com.algolens.algo_lens.dtos.code.DiffDeltaDTO;
import com.algolens.algo_lens.dtos.code.SubmissionCodeDTO;
import com.algolens.algo_lens.dtos.code.raw.ContestSubmissionDTO;
import com.github.difflib.patch.Patch;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CodeMapper {

    public CommonContestDTO mapToCommonContestDTO(int contestId,
                                                  String contestName) {
        return CommonContestDTO.builder()
                .contestId(contestId)
                .contestName(contestName)
                .build();
    }

    public SubmissionCodeDTO mapToSubmissionCodeDTO(
            ContestSubmissionDTO submission,
            String code,
            List<DiffDeltaDTO> diff
    ) {
        return SubmissionCodeDTO.builder()
                .handle(submission.getHandle())
                .problemIndex(submission.getProblem() != null
                        ? submission.getProblem().getIndex() : null)
                .language(submission.getProgrammingLanguage())
                .code(code)
                .verdict(submission.getVerdict())
                .timeConsumedMillis(submission.getTimeConsumedMillis())
                .memoryConsumedBytes(submission.getMemoryConsumedBytes())
                .submissionId(submission.getId())
                .diff(diff)
                .build();
    }

    public List<DiffDeltaDTO> mapToDiffDeltas(Patch<String> patch) {
        return patch.getDeltas().stream()
                .map(delta -> DiffDeltaDTO.builder()
                        .type(delta.getType().name())
                        .position(delta.getSource().getPosition())
                        .originalLines(delta.getSource().getLines())
                        .newLines(delta.getTarget().getLines())
                        .build())
                .collect(Collectors.toList());
    }
}
