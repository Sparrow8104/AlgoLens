package com.algolens.algo_lens.controller;


import com.algolens.algo_lens.dtos.analysis.AiAnalysisResponseDTO;
import com.algolens.algo_lens.services.service.AnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final AnalysisService aiAnalysisService;
    @GetMapping("/upsolve/{handle}")
    public ResponseEntity<AiAnalysisResponseDTO> analyzeUpsolve(@PathVariable String handle) {
        return ResponseEntity.ok(aiAnalysisService.analyzeUpsolve(handle));
    }




}
