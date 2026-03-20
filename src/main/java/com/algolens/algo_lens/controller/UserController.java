package com.algolens.algo_lens.controller;

import com.algolens.algo_lens.dtos.ContestDTO;
import com.algolens.algo_lens.dtos.RatingGraphDTO;
import com.algolens.algo_lens.dtos.SubmissionStatsDTO;
import com.algolens.algo_lens.dtos.user.userInfo.UserProfileDTO;
import com.algolens.algo_lens.services.service.UserServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {


    private  final UserServices userServices;
    public UserController(UserServices userServicesServices) {
    this.userServices = userServicesServices;
    }

    @GetMapping("/{handle}/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable String handle) {
        UserProfileDTO userProfile =userServices.getUserProfile(handle);
        return ResponseEntity.ok(userProfile);
    }

    @GetMapping("/{handle}/contest-history")
    public ResponseEntity<List<ContestDTO>> getUserContestHistory(@PathVariable String handle){
        List<ContestDTO> contests = userServices.getUserContestHistory(handle);
        return ResponseEntity.ok(contests);
    }

    @GetMapping("/{handle}/rating-graph")
    public ResponseEntity<List<RatingGraphDTO>> getUserRatingGraph(@PathVariable String handle){
        List<RatingGraphDTO> ratings = userServices.getUserRatingGraph(handle);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/{handle}/submission-stats")
    public ResponseEntity<SubmissionStatsDTO> getUserSubmissionStats(@PathVariable String handle){
        SubmissionStatsDTO stats=userServices.getSubmissionStats(handle);
        return ResponseEntity.ok(stats);
    }


}
