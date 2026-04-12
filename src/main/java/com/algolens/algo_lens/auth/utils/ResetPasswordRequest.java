package com.algolens.algo_lens.auth.utils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank String token,

        @NotBlank
        @Size(min = 8, message = "Password must be at least 8 characters")
        String newPassword
) {}