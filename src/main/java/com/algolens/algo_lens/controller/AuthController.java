package com.algolens.algo_lens.controller;


import com.algolens.algo_lens.auth.services.AuthService;
import com.algolens.algo_lens.auth.utils.AuthResponse;
import com.algolens.algo_lens.auth.utils.RegisterRequest;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        AuthResponse response=authService.register(registerRequest);
        return ResponseEntity.ok(response);
    }
}
