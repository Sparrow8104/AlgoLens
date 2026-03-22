package com.algolens.algo_lens.mapper;

import com.algolens.algo_lens.dtos.insight.RecommendationDTO;
import com.algolens.algo_lens.dtos.insight.UpsolveDTO;
import com.algolens.algo_lens.dtos.insight.WeakTopicDTO;
import com.algolens.algo_lens.dtos.user.userStatus.ProblemDTO;
import com.algolens.algo_lens.dtos.user.userStatus.SubmissionDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InsightMapper {

    public WeakTopicDTO mapToWeakTopicDTO(
            String tag,
            int totalAttempts,
            int solvedCount
    ) {
        return WeakTopicDTO.builder()
                .tag(tag)
                .totalAttempts(totalAttempts)
                .solvedCount(solvedCount)
                .unsolvedCount(totalAttempts - solvedCount)
                .acRate(totalAttempts == 0 ? 0.0 :
                        Math.round((double) solvedCount / totalAttempts * 100.0 * 10) / 10.0)
                .build();
    }

    public RecommendationDTO mapToRecommendationDTO(ProblemDTO problem) {
        return RecommendationDTO.builder()
                .contestId(problem.getContestId())
                .index(problem.getIndex())
                .name(problem.getName())
                .rating(problem.getRating())
                .tags(problem.getTags())
                .build();
    }

    public UpsolveDTO mapToUpsolveDTO(ProblemDTO problem, String bestVerdict) {
        return UpsolveDTO.builder()
                .contestId(problem.getContestId())
                .index(problem.getIndex())
                .name(problem.getName())
                .rating(problem.getRating())
                .tags(problem.getTags())
                .bestVerdict(bestVerdict)
                .build();
    }
}