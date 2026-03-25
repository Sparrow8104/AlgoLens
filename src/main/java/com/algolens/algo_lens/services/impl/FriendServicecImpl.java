package com.algolens.algo_lens.services.impl;

import com.algolens.algo_lens.client.CodeforcesApiClient;
import com.algolens.algo_lens.dtos.friend.*;
import com.algolens.algo_lens.mapper.FriendMapper;
import com.algolens.algo_lens.models.Friend;
import com.algolens.algo_lens.repository.FriendRepository;
import com.algolens.algo_lens.repository.UserFriendRepository;
import com.algolens.algo_lens.services.service.FriendServices;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendServicecImpl implements FriendServices {

    private final UserFriendRepository userFriendRepository;
    private final CodeforcesApiClient codeforcesApiClient;
    private final FriendMapper friendMapper;

    public FriendServicecImpl(UserFriendRepository userFriendRepository, CodeforcesApiClient codeforcesApiClient, FriendMapper friendMapper) {
        this.userFriendRepository = userFriendRepository;
        this.codeforcesApiClient = codeforcesApiClient;
        this.friendMapper = friendMapper;
    }

    @Override
    public void addFriend(String userHandle, String friendHandle) {

    }

    @Override
    public void removeFriend(String userHandle, String friendHandle) {

    }

    @Override
    public List<FriendDTO> getFriends(String handle) {
        return List.of();
    }

    @Override
    public List<LeaderboardEntryDTO> getLeaderboard(String handle) {
        return List.of();
    }

    @Override
    public List<UnsolvedByMeDTO> getUnsolvedByMe(String handle) {
        return List.of();
    }

    @Override
    public List<ContestOverlapDTO> getContestOverlap(String handle, int contestId) {
        return List.of();
    }

    @Override
    public List<StreakCompareDTO> getStreakComparison(String handle) {
        return List.of();
    }
}