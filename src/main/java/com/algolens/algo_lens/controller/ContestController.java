package com.algolens.algo_lens.controller;

import com.algolens.algo_lens.dtos.contest.CodeforcesContestItemDTO;
import com.algolens.algo_lens.dtos.contest.CodeforcesContestResponseDTO;
import com.algolens.algo_lens.dtos.contest.UpcomingContestDTO;
import com.algolens.algo_lens.services.service.ContestServices;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RestController
@RequestMapping("/api/contests")
public class ContestController {
    private final ContestServices contestServices;
    private final WebClient webClient;
    public ContestController(ContestServices contestServices, WebClient webClient) {
        this.contestServices = contestServices;
        this.webClient = webClient;
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get Codeforces upcoming contests")
    public ResponseEntity<List<UpcomingContestDTO>> upcomingContests() {
        List<UpcomingContestDTO> upcoming=contestServices.getUpcomingContests();
        return ResponseEntity.ok().body(upcoming);
    }

    @GetMapping("/upcoming/paginated")
    @Operation(summary = "Get Codeforces upcoming contests paginated")
    public ResponseEntity<Page<UpcomingContestDTO>> upcomingContestsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok().body(contestServices.getUpcomingContestsPaginated(PageRequest.of(page, size)));
    }

    @GetMapping
    @Operation(summary = "Get Codeforces all contests")
    public ResponseEntity<Page<UpcomingContestDTO>>  getAllContests(
            @RequestParam(defaultValue = "0") int page
            , @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok().body(contestServices.getAllContestsPaginated(PageRequest.of(page, size)));
    }

//    @GetMapping("/test-cf")
//    public ResponseEntity<String> testCf() {
//        String raw = webClient.get()
//                .uri("https://codeforces.com/api/contest.list?gym=false")
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//
//        if (raw == null) return ResponseEntity.ok("NULL RESPONSE");
//        return ResponseEntity.ok("LENGTH: " + raw.length() + " | FIRST 200: " + raw.substring(0, 200));
//    }

//    public ResponseEntity<List<CodeforcesContestItemDTO>> getAllContests(){
//        List<CodeforcesContestItemDTO> contests = contestServices.fetchAllContests();
//        return ResponseEntity.ok().body(contests);
//    }
}
