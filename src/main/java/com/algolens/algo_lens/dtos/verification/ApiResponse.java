package com.algolens.algo_lens.dtos.verification;

import com.algolens.algo_lens.controller.VerificationController;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor(staticName = "of")
public class ApiResponse {
    private final String code;
    private final String message;
    private final boolean success;

    public static ApiResponse success(String code, String message) {
        return ApiResponse.of(code, message, true);
    }

    public static ApiResponse error(String code, String message) {
        return ApiResponse.of(code, message, false);
    }
}