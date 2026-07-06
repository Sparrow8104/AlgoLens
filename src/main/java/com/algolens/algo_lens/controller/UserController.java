package com.algolens.algo_lens.controller;

import com.algolens.algo_lens.dtos.ContestDTO;
import com.algolens.algo_lens.dtos.RatingGraphDTO;
import com.algolens.algo_lens.dtos.SubmissionStatsDTO;
import com.algolens.algo_lens.dtos.user.userInfo.UserProfileDTO;
import com.algolens.algo_lens.services.service.UserServices;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {


    private  final UserServices userServices;
    public UserController(UserServices userServicesServices) {
    this.userServices = userServicesServices;
    }

    @GetMapping("/{handle}/profile")
    @Operation(summary = "Get Codeforces user profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable String handle) {
        UserProfileDTO userProfile =userServices.getUserProfile(handle);
        return ResponseEntity.ok(userProfile);
    }

    @GetMapping("/{handle}/rating-graph")
    @Operation(summary = "Get Codeforces user rating graph")
    public ResponseEntity<List<RatingGraphDTO>> getUserRatingGraph(@PathVariable String handle){
        List<RatingGraphDTO> ratings = userServices.getUserRatingGraph(handle);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/{handle}/submission-stats")
    @Operation(summary = "Get Codeforces user submissions")
    public ResponseEntity<SubmissionStatsDTO> getUserSubmissionStats(@PathVariable String handle){
        SubmissionStatsDTO stats=userServices.getSubmissionStats(handle);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{handle}/contest-history/paginated")
    @Operation(summary = "Get Codeforces user contest history (paginated)")
    public ResponseEntity<Page<ContestDTO>> getUserContestHistoryPaginated(
            @PathVariable String handle,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ){
        return ResponseEntity.ok(userServices.getUserContestHistoryPaginated(handle, PageRequest.of(page, size)));
    }
}
