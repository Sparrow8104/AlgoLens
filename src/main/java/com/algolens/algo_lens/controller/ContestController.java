package com.algolens.algo_lens.controller;

import com.algolens.algo_lens.dtos.contest.CodeforcesContestItemDTO;
import com.algolens.algo_lens.dtos.contest.CodeforcesContestResponseDTO;
import com.algolens.algo_lens.dtos.contest.UpcomingContestDTO;
import com.algolens.algo_lens.services.service.ContestServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ResponseEntity<List<UpcomingContestDTO>> upcomingContests() {
        List<UpcomingContestDTO> upcoming=contestServices.getUpcomingContests();
        return ResponseEntity.ok().body(upcoming);
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
