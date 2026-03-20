package com.algolens.algo_lens.controller;


import com.algolens.algo_lens.dtos.comparison.RatingComparisonDTO;
import com.algolens.algo_lens.dtos.comparison.SubmissionCompareRequestDTO;
import com.algolens.algo_lens.dtos.comparison.SubmissionCompareResponseDTO;
import com.algolens.algo_lens.services.service.ComparisonServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/compare")
public class ComparisonController {

    private final ComparisonServices comparisonServices;

    public ComparisonController(ComparisonServices comparisonServices) {
        this.comparisonServices = comparisonServices;
    }

    @GetMapping("/rating")
    public ResponseEntity<RatingComparisonDTO> compareRatings(@RequestParam String handle1,@RequestParam String handle2) {
        RatingComparisonDTO compare=comparisonServices.compareRatings(handle1, handle2);
        return ResponseEntity.ok().body(compare);
    }

    @PostMapping("/find")
    public ResponseEntity<SubmissionCompareResponseDTO> findSubmissions(@RequestBody SubmissionCompareRequestDTO request) {
        SubmissionCompareResponseDTO responseDTO=comparisonServices.findSubmissions(request);
        return ResponseEntity.ok().body(responseDTO);
    }
}
