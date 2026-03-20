package com.algolens.algo_lens.mapper;

import com.algolens.algo_lens.dtos.comparison.RatingComparisonDTO;
import com.algolens.algo_lens.dtos.comparison.SubmissionCompareResponseDTO;
import com.algolens.algo_lens.dtos.comparison.UserSubmissionResultDTO;
import com.algolens.algo_lens.dtos.user.userInfo.CodeforcesUserDTO;
import com.algolens.algo_lens.dtos.user.userStatus.SubmissionDTO;
import org.springframework.stereotype.Component;

@Component
public class ComparisonMapper {
    public RatingComparisonDTO mapToRatingComparisonDTO(CodeforcesUserDTO user1,
                                                        CodeforcesUserDTO user2,
                                                        int contestsParticipated1,
                                                        int contestsParticipated2) {
        int rating1 = user1.getRating()!=null?user1.getRating():0;
        int rating2 = user2.getRating()!=null?user2.getRating():0;
        int delta=rating1-rating2;

        String higherRated=delta>0? user1.getHandle():delta<0?user2.getHandle():"equal";

        return RatingComparisonDTO.builder()
                .handle1(user1.getHandle())
                .handle2(user2.getHandle())
                .rating1(user1.getRating())
                .rating2(user2.getRating())
                .ratingDelta(Math.abs(delta))
                .higherRatedHandle(higherRated)
                .maxRating1(user1.getMaxRating())
                .maxRating2(user2.getMaxRating())
                .rank1(user1.getRank())
                .rank2(user2.getRank())
                .contestsParticipated1(contestsParticipated1)
                .contestsParticipated2(contestsParticipated2)
                .build();
    }

    public UserSubmissionResultDTO mapToUserSubmissionResultDTO(String handle,
                                                                SubmissionDTO submission ) {
        if(submission==null) {
            return UserSubmissionResultDTO.builder()
                    .handle(handle)
                    .solved(false)
                    .verdict("NOT_ATTEMPTED")
                    .programmingLanguage(null)
                    .timeConsumedMillis(null)
                    .memoryConsumedBytes(null)
                    .submissionId(null)
                    .submittedAt(null)
                    .build();
        }
        return UserSubmissionResultDTO.builder()
                .handle(handle)
                .solved("OK".equals(submission.getVerdict()))
                .verdict(submission.getVerdict())
                .programmingLanguage(submission.getProgrammingLanguage())
                .timeConsumedMillis(submission.getTimeConsumedMillis())
                .memoryConsumedBytes(submission.getMemoryConsumedBytes())
                .submissionId(submission.getId())
                .submittedAt(submission.getCreationTimeSeconds())
                .build();
    }

    public SubmissionCompareResponseDTO mapToSubmissionCompareResponseDTO(int contestId,
                                                                          String index,
                                                                          UserSubmissionResultDTO user1Result,
                                                                          UserSubmissionResultDTO user2Result
                                                                        ) {
        return SubmissionCompareResponseDTO.builder()
                .contestId(contestId)
                .index(index)
                .user1Result(user1Result)
                .user2Result(user2Result)
                .build();
    }
}
