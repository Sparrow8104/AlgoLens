package com.algolens.algo_lens.controller;

import com.algolens.algo_lens.dtos.insight.RecommendationDTO;
import com.algolens.algo_lens.dtos.insight.UpsolveDTO;
import com.algolens.algo_lens.dtos.insight.WeakTopicDTO;
import com.algolens.algo_lens.services.service.InsightServices;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/insights")
public class InsightController {
    private final InsightServices insightServices;

    public InsightController(InsightServices insightServices) {
        this.insightServices = insightServices;
    }

    @GetMapping("/{handle}/weak-topics")
    @Operation(summary = "Get user's weak topic to practice")
    public ResponseEntity<List<WeakTopicDTO>> getWeakTopics(@PathVariable String handle) {
        return ResponseEntity.ok().body(insightServices.getWeakTopics(handle));
    }

    @GetMapping("/{handle}/recommendations")
    @Operation(summary = "Get the recommendations to solve based on weak topics")
    public ResponseEntity<List<RecommendationDTO>> getRecommendations(@PathVariable String handle) {
        return ResponseEntity.ok().body(insightServices.getRecommendations(handle));
    }
    @GetMapping("/{handle}/upsolve")
    @Operation(summary = "Get the problems to upsolve from latest contests")
    public ResponseEntity<Map<Integer, List<UpsolveDTO>>> getUpsolveContests(
            @PathVariable String handle
    ) {
        return ResponseEntity.ok(insightServices.getUpsolveContests(handle));
    }


}
