package com.algolens.algo_lens.controller;


import com.algolens.algo_lens.dtos.code.CommonContestDTO;
import com.algolens.algo_lens.services.service.CodeServices;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/code")
public class CodeController {

    private final CodeServices codeServices;

    public CodeController(CodeServices codeServices) {
        this.codeServices = codeServices;
    }

    @GetMapping("/common-contests")
    public ResponseEntity<List<CommonContestDTO>> getCommonContests(
            @RequestParam String handle1,
            @RequestParam String handle2
    ) {
        return ResponseEntity.ok(codeServices.getCommonContests(handle1, handle2));
    }
}
