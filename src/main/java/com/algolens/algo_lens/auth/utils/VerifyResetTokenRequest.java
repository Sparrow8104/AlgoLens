package com.algolens.algo_lens.auth.utils;

import jakarta.validation.constraints.NotBlank;

public record VerifyResetTokenRequest(
        @NotBlank String token
) {}