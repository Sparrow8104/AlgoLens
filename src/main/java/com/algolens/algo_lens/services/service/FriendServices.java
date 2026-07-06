package com.algolens.algo_lens.services.service;

import com.algolens.algo_lens.dtos.friend.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FriendServices {
    void addFriend(String userHandle, String friendHandle);
    void removeFriend(String userHandle, String friendHandle);
    List<FriendDTO> getFriends(String handle);
    List<LeaderboardEntryDTO> getLeaderboard(String handle);
    List<UnsolvedByMeDTO> getUnsolvedByMe(String handle);
    List<ContestOverlapDTO> getContestOverlap(String handle, int contestId);
    List<StreakCompareDTO> getStreakComparison(String handle);
    
    Page<FriendDTO> getFriendsPaginated(String handle, Pageable pageable);
    Page<LeaderboardEntryDTO> getLeaderboardPaginated(String handle, Pageable pageable);
    Page<UnsolvedByMeDTO> getUnsolvedByMePaginated(String handle, Pageable pageable);
    Page<ContestOverlapDTO> getContestOverlapPaginated(String handle, int contestId, Pageable pageable);
    Page<StreakCompareDTO> getStreakComparisonPaginated(String handle, Pageable pageable);
}
