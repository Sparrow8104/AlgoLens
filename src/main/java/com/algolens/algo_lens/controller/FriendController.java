package com.algolens.algo_lens.controller;

import com.algolens.algo_lens.dtos.friend.FriendDTO;
import com.algolens.algo_lens.dtos.friend.FriendRequestDTO;
import com.algolens.algo_lens.services.service.FriendServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendController {

    private final FriendServices friendService;

    public FriendController(FriendServices friendService) {
        this.friendService = friendService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addFriend(
            @RequestBody FriendRequestDTO request
    ) {
        friendService.addFriend(request.userHandle(), request.friendHandle());
        return ResponseEntity.ok("Friend added successfully");
    }

    @DeleteMapping("/{userHandle}/remove/{friendHandle}")
    public ResponseEntity<String> removeFriend(
            @PathVariable String userHandle,
            @PathVariable String friendHandle
    ) {
        friendService.removeFriend(userHandle, friendHandle);
        return ResponseEntity.ok("Friend removed successfully");
    }

    @GetMapping("/{handle}")
    public ResponseEntity<List<FriendDTO>> getFriends(
            @PathVariable String handle
    ) {
        return ResponseEntity.ok(friendService.getFriends(handle));
    }
}