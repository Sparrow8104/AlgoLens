package com.algolens.algo_lens.auth.utils;

import lombok.Builder;

@Builder
public record RegisterRequest(
        String name,
        String email,
        String password
) {

}
