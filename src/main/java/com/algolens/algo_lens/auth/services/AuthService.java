package com.algolens.algo_lens.auth.services;


import com.algolens.algo_lens.auth.entities.EmailVerificationToken;
import com.algolens.algo_lens.auth.entities.RefreshToken;
import com.algolens.algo_lens.auth.entities.UserRole;
import com.algolens.algo_lens.auth.exception.UserAlreadyExistsException;
import com.algolens.algo_lens.auth.repositories.EmailVerificationTokenRepository;
import com.algolens.algo_lens.auth.repositories.RefreshTokenRepository;
import com.algolens.algo_lens.auth.repositories.UserRepository;
import com.algolens.algo_lens.auth.utils.AuthRequest;
import com.algolens.algo_lens.auth.utils.AuthResponse;
import com.algolens.algo_lens.auth.utils.RefreshTokenRequest;
import com.algolens.algo_lens.auth.utils.RegisterRequest;
import com.algolens.algo_lens.auth.entities.User;
import com.algolens.algo_lens.exception.UserNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailService emailService;

    public AuthService(PasswordEncoder passwordEncoder, JwtService jwtService, UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, RefreshTokenService refreshTokenService, AuthenticationManager authenticationManager, EmailVerificationTokenRepository emailVerificationTokenRepository, EmailService emailService) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.emailService = emailService;
    }

    @Transactional
    public String register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.email()).isPresent()) {
            throw new UserAlreadyExistsException("Email already registered: "+registerRequest.email());
        }
        var user= User.builder()
                .name(registerRequest.name())
                .password(passwordEncoder.encode(registerRequest.password()))
                .email(registerRequest.email())
                .role(UserRole.USER)
                .emailVerified(false)
                .build();
        userRepository.save(user);

        String token= UUID.randomUUID().toString();

        EmailVerificationToken emailVerificationToken = EmailVerificationToken.builder()
                .token(token)
                .user(user)
                .expiresAt(Instant.now().plusSeconds(86400))
                .build();

        emailVerificationTokenRepository.save(emailVerificationToken);
        emailService.sendEmailVerification(user.getEmail(), token);
        return "Registration successful. Please check you email to verify your account";
    }

    @Transactional
    public String verifyEmail(String token){
        EmailVerificationToken verificationToken=emailVerificationTokenRepository.findByToken(token)
                .orElseThrow(()->new RuntimeException("Invalid or expired token"));

        if(verificationToken.getExpiresAt().isBefore(Instant.now())){
            emailVerificationTokenRepository.delete(verificationToken);
            throw new RuntimeException("Verification Link has expired.Please register again");
        }

        User user=verificationToken.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        emailVerificationTokenRepository.delete(verificationToken);

        return "Email verified successfully.You can now log in.";
    }

    public AuthResponse login(AuthRequest authRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.email(), authRequest.password()));
        var user= userRepository.findByEmail(authRequest.email())
                .orElseThrow(()->new UsernameNotFoundException("User not found: "+authRequest.email()));
        if(!user.isEmailVerified()){
            throw new RuntimeException("Please verify your email address before logging in.");
        }
        var accessToken=jwtService.generateToken(user);
        var refreshToken=refreshTokenService.createRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken verified = refreshTokenService.verifyRefreshToken(request.refreshToken());
        RefreshToken rotated = refreshTokenService.rotateRefreshToken(verified);

        User user = verified.getUser();
        String newAccessToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(rotated.getRefreshToken())
                .build();
    }

    @Transactional
    public String logout(String userEmail) {
        User user=userRepository.findByEmail(userEmail)
                .orElseThrow(()->new UsernameNotFoundException("User not found: "+userEmail));
        refreshTokenRepository.deleteByUser(user);
        return "Logged out successfully";
    }
}
