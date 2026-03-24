package com.algolens.algo_lens.services.service;

import java.util.List;

public interface FriendService {
    void addFriend(String userHandle, String friendHandle);
    void removeFriend(String userHandle, String friendHandle);
    List<String> getFriends(String userHandle);
}
