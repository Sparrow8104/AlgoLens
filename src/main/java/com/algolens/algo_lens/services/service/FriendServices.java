package com.algolens.algo_lens.services.service;

import com.algolens.algo_lens.dtos.friend.*;

import java.util.List;

public interface FriendServices {
    void addFriend(String userHandle, String friendHandle);
    void removeFriend(String userHandle, String friendHandle);
    List<FriendDTO> getFriends(String handle);
    List<LeaderboardEntryDTO> getLeaderboard(String handle);
    List<UnsolvedByMeDTO> getUnsolvedByMe(String handle);
    List<ContestOverlapDTO> getContestOverlap(String handle, int contestId);
    List<StreakCompareDTO> getStreakComparison(String handle);
}
