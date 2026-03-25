package com.algolens.algo_lens.controller;

import com.algolens.algo_lens.dtos.friend.*;
import com.algolens.algo_lens.services.service.FriendServices;
import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendController {

    private final FriendServices friendServices;

    public FriendController(FriendServices friendService) {
        this.friendServices = friendService;
    }

    @PostMapping("/add")
    @Operation(summary = "Add a friend to compare with")
    public ResponseEntity<String> addFriend(
            @RequestBody FriendRequestDTO request
    ) {
        friendServices.addFriend(request.userHandle(), request.friendHandle());
        return ResponseEntity.ok("Friend added successfully");
    }

    @DeleteMapping("/{userHandle}/remove/{friendHandle}")
    @Operation(summary = "Delete the friend from database")
    public ResponseEntity<String> removeFriend(
            @PathVariable String userHandle,
            @PathVariable String friendHandle
    ) {
        friendServices.removeFriend(userHandle, friendHandle);
        return ResponseEntity.ok("Friend removed successfully");
    }

    @GetMapping("/{handle}")
    @Operation(summary = "Get all friends that user added")
    public ResponseEntity<List<FriendDTO>> getFriends(
            @PathVariable String handle
    ) {
        return ResponseEntity.ok(friendServices.getFriends(handle));
    }

    @GetMapping("/{handle}/leaderboard")
    @Operation(summary = "Get the leaderboard that shows comparison with all friends")
    public ResponseEntity<List<LeaderboardEntryDTO>> getLeaderboard(
            @PathVariable String handle
    ){
        return ResponseEntity.ok(friendServices.getLeaderboard(handle));
    }

    @GetMapping("/{handle}/unsolved-by-me")
    @Operation(summary = "Get the problems that user did not solved")
    public ResponseEntity<List<UnsolvedByMeDTO>> getUnsolvedByMe(
            @PathVariable String handle
    ){
        return ResponseEntity.ok(friendServices.getUnsolvedByMe(handle));
    }

    @GetMapping("/{handle}/streak-compare")
    @Operation(summary = "Compare streak with friends")
    public ResponseEntity<List<StreakCompareDTO>> getStreakCompare(@PathVariable String handle){
        return ResponseEntity.ok(friendServices.getStreakComparison(handle));
    }

    @GetMapping("/{handle}/contest-overlap/{contestId}")
    @Operation(summary = "Get the probelem from an overlapping contests")
    public ResponseEntity<List<ContestOverlapDTO>> getContestOverlap(@PathVariable String handle, @PathVariable int contestId){
        return ResponseEntity.ok(friendServices.getContestOverlap(handle, contestId));
    }
}