package com.algolens.algo_lens.controller;

import com.algolens.algo_lens.dtos.insight.RecommendationDTO;
import com.algolens.algo_lens.dtos.insight.WeakTopicDTO;
import com.algolens.algo_lens.services.service.InsightServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/insights")
public class InsightController {
    private final InsightServices insightServices;

    public InsightController(InsightServices insightServices) {
        this.insightServices = insightServices;
    }

    @GetMapping("/{handle}/weak-topics")
    public ResponseEntity<List<WeakTopicDTO>> getWeakTopics(@PathVariable String handle) {
        return ResponseEntity.ok().body(insightServices.getWeakTopics(handle));
    }

    @GetMapping("/{handle}/recommendations")
    public ResponseEntity<List<RecommendationDTO>> getRecommendations(@PathVariable String handle) {
        return ResponseEntity.ok().body(insightServices.getRecommendations(handle));
    }


}
