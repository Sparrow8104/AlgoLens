package com.algolens.algo_lens.auth.services;


import com.algolens.algo_lens.auth.entities.UserRole;
import com.algolens.algo_lens.auth.exception.UserAlreadyExistsException;
import com.algolens.algo_lens.auth.repositories.RefreshTokenRepository;
import com.algolens.algo_lens.auth.repositories.UserRepository;
import com.algolens.algo_lens.auth.utils.AuthResponse;
import com.algolens.algo_lens.auth.utils.RegisterRequest;
import com.algolens.algo_lens.auth.entities.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;

    public AuthService(PasswordEncoder passwordEncoder, JwtService jwtService, UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, RefreshTokenService refreshTokenService) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthResponse register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.email()).isPresent()) {
            throw new UserAlreadyExistsException("Email already registered: "+registerRequest.email());
        }
        var user= User.builder()
                .name(registerRequest.name())
                .password(passwordEncoder.encode(registerRequest.password()))
                .email(registerRequest.email())
                .role(UserRole.USER)
                .build();
        User savedUser=userRepository.save(user);
        var accessToken=jwtService.generateToken(savedUser);
        var refreshToken=refreshTokenService.createRefreshToken(savedUser.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }
}
