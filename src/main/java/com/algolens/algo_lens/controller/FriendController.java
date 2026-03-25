package com.algolens.algo_lens.controller;

import com.algolens.algo_lens.services.service.FriendServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
public class FriendController {

    private final FriendServices friendService;

    public FriendController(FriendServices friendService) {
        this.friendService = friendService;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addFriend(
            @RequestParam String userHandle,
            @RequestParam String friendHandle
    ) {
        friendService.addFriend(userHandle, friendHandle);
        return ResponseEntity.ok("UserFriend added");
    }

    @DeleteMapping("/{userHandle}/remove/{friendHandle}")
    public ResponseEntity<String> removeFriend(
            @PathVariable String userHandle,
            @PathVariable String friendHandle
    ) {
        friendService.removeFriend(userHandle, friendHandle);
        return ResponseEntity.ok("UserFriend removed");
    }

    @GetMapping("/{userHandle}")
    public ResponseEntity<List<String>> getFriends(
            @PathVariable String userHandle
    ) {
        return ResponseEntity.ok(friendService.getFriends(userHandle));
    }
}