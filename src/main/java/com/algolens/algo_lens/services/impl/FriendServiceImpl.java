package com.algolens.algo_lens.services.impl;

import com.algolens.algo_lens.models.Friend;
import com.algolens.algo_lens.repository.FriendRepository;
import com.algolens.algo_lens.services.service.FriendService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendServiceImpl implements FriendService {

    private final FriendRepository friendRepository;

    public FriendServiceImpl(FriendRepository friendRepository) {
        this.friendRepository = friendRepository;
    }

    @Override
    public void addFriend(String userHandle, String friendHandle) {
        if (friendRepository.existsByUserHandleAndFriendHandle(userHandle, friendHandle)) {
            throw new RuntimeException("Friend already added");
        }

        Friend friend = new Friend(userHandle, friendHandle);
        friendRepository.save(friend);
    }

    @Override
    public void removeFriend(String userHandle, String friendHandle) {
        friendRepository.deleteByUserHandleAndFriendHandle(userHandle, friendHandle);
    }

    @Override
    public List<String> getFriends(String userHandle) {
        return friendRepository.findByUserHandle(userHandle)
                .stream()
                .map(Friend::getFriendHandle)
                .toList();
    }
}