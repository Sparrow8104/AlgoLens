package com.algolens.algo_lens.mapper;

import com.algolens.algo_lens.dtos.friend.ContestOverlapDTO;
import com.algolens.algo_lens.dtos.friend.FriendDTO;
import com.algolens.algo_lens.dtos.friend.LeaderboardEntryDTO;
import com.algolens.algo_lens.dtos.friend.StreakCompareDTO;
import com.algolens.algo_lens.dtos.user.userInfo.CodeforcesUserDTO;
import com.algolens.algo_lens.dtos.user.userRating.RatingChangeDTO;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;

@Component
public class FriendMapper {

    public FriendDTO mapToFriendDTO(
            CodeforcesUserDTO user,
            int contestsParticipated
    ){
        return FriendDTO.builder()
                .handle(user.getHandle())
                .rating(user.getRating())
                .maxRating(user.getMaxRating())
                .rank(user.getRank())
                .avatar(user.getAvatar())
                .contestsParticipated(contestsParticipated)
                .build();
    }

    public LeaderboardEntryDTO mapToLeaderboardEntryDTO(
            int rank,
            CodeforcesUserDTO user
    ){
        return LeaderboardEntryDTO.builder()
                .rank(rank)
                .handle(user.getHandle())
                .rating(user.getRating())
                .tier(user.getRank())
                .maxRating(user.getMaxRating())
                .build();
    }
    public ContestOverlapDTO mapToContestOverlapDTO(RatingChangeDTO ratingChange) {
        return ContestOverlapDTO.builder()
                .handle(ratingChange.getHandle())
                .rank(ratingChange.getRank())
                .oldRating(ratingChange.getOldRating())
                .newRating(ratingChange.getNewRating())
                .ratingChange(ratingChange.getNewRating() - ratingChange.getOldRating())
                .build();
    }

    public StreakCompareDTO mapToStreakCompareDTO(
            String handle,
            int streak,
            Long lastSubmissionTime
    ) {
        String lastDate = lastSubmissionTime == null ? "never" :
                Instant.ofEpochSecond(lastSubmissionTime)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .toString();

        return StreakCompareDTO.builder()
                .handle(handle)
                .currentStreak(streak)
                .lastSubmissionDate(lastDate)
                .build();
    }

}
