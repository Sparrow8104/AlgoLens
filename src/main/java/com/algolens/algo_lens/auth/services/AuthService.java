package com.algolens.algo_lens.auth.services;


import com.algolens.algo_lens.auth.entities.*;
import com.algolens.algo_lens.auth.exception.EmailNotVerifiedException;
import com.algolens.algo_lens.auth.exception.InvalidTokenException;
import com.algolens.algo_lens.auth.exception.TokenExpiredException;
import com.algolens.algo_lens.auth.exception.UserAlreadyExistsException;
import com.algolens.algo_lens.auth.repositories.PendingRegistrationRepository;
import com.algolens.algo_lens.auth.repositories.RefreshTokenRepository;
import com.algolens.algo_lens.auth.repositories.UserRepository;
import com.algolens.algo_lens.auth.utils.AuthRequest;
import com.algolens.algo_lens.auth.utils.AuthResponse;
import com.algolens.algo_lens.auth.utils.RefreshTokenRequest;
import com.algolens.algo_lens.auth.utils.RegisterRequest;
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

    private static final long VERIFICATION_TTL_SECONDS = 86_400;

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final RateLimiterService rateLimiterService;
    private final LoginAttemptService loginAttemptService;

    private final PendingRegistrationRepository pendingRegistrationRepository;

    public AuthService(PasswordEncoder passwordEncoder, JwtService jwtService, UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, RefreshTokenService refreshTokenService, AuthenticationManager authenticationManager, EmailService emailService, RateLimiterService rateLimiterService, LoginAttemptService loginAttemptService, PendingRegistrationRepository pendingRegistrationRepository) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
        this.emailService = emailService;
        this.rateLimiterService = rateLimiterService;
        this.loginAttemptService = loginAttemptService;
        this.pendingRegistrationRepository = pendingRegistrationRepository;
    }

    @Transactional
    public String register(RegisterRequest registerRequest,String ip) {

        String email = registerRequest.email().trim().toLowerCase();
        var existingUser=userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("Email already registered: " + registerRequest.email());
        }
        rateLimiterService.checkAndRecordEmail(email, ip);

        String token = UUID.randomUUID().toString();
        String hashedToken = refreshTokenService.hashToken(token);
        Instant expiry = Instant.now().plusSeconds(VERIFICATION_TTL_SECONDS);

        PendingRegistration pending = pendingRegistrationRepository
                .findByEmail(email)
                .map(existing -> {
                    existing.setName(registerRequest.name().trim());
                    existing.setEncodedPassword(passwordEncoder.encode(registerRequest.password()));
                    existing.setExpiresAt(expiry);
                    existing.setToken(hashedToken);
                    return existing;
                })
                .orElseGet(() -> PendingRegistration.builder()
                        .email(email)
                        .name(registerRequest.name().trim())
                        .encodedPassword(passwordEncoder.encode(registerRequest.password()))
                        .token(hashedToken)
                        .expiresAt(expiry)
                        .build());

        pendingRegistrationRepository.save(pending);
        emailService.sendEmailVerification(email, token);

        return "Registration successful. Please check you email to verify your account";
    }

    @Transactional
    public String verifyEmail(String rawToken){

        String tokenHash = refreshTokenService.hashToken(rawToken);
        PendingRegistration pending = pendingRegistrationRepository.findByToken(tokenHash)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired verification link."));

        if (pending.getExpiresAt().isBefore(Instant.now())) {
            pendingRegistrationRepository.delete(pending);
            throw new TokenExpiredException(
                    "Verification link has expired. Please request a new one.");
        }

        if (userRepository.findByEmail(pending.getEmail()).isPresent()) {
            pendingRegistrationRepository.delete(pending);
            return "Email already verified. You can log in.";
        }

        User user = User.builder()
                .name(pending.getName())
                .email(pending.getEmail())
                .password(pending.getEncodedPassword())
                .role(UserRole.USER)
                .emailVerified(true)
                .build();

        userRepository.save(user);
        pendingRegistrationRepository.delete(pending);

        return "Email verified successfully. You can now log in.";
    }

    @Transactional
    public AuthResponse login(AuthRequest authRequest, String deviceId, String userAgent, String ip) {
        String email = authRequest.email().trim().toLowerCase();


        loginAttemptService.checkBlocked(ip);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, authRequest.password()));
        } catch (Exception ex) {
            loginAttemptService.recordFailure(ip);
            throw ex;
        }

        loginAttemptService.clearFailures(ip);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException(
                    "Please verify your email address before logging in.");
        }

        String accessToken = jwtService.generateToken(user);

        String rawRefreshToken = refreshTokenService.createRefreshToken(user, deviceId, userAgent, ip);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rawRefreshToken)
                .build();
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken verified = refreshTokenService.verifyRefreshToken(request.refreshToken());
        String newRawRefreshToken = refreshTokenService.rotateRefreshToken(verified);

        User user = verified.getUser();
        String newAccessToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRawRefreshToken)
                .build();
    }

    @Transactional
    public String logout(String rawRefreshToken) {
        String hashedToken = refreshTokenService.hashToken(rawRefreshToken);

        refreshTokenRepository.findByRefreshToken(hashedToken)
                .ifPresent(refreshTokenRepository::delete);

        return "Logged out successfully.";
    }

    @Transactional
    public String logoutAll(String userEmail) {
        userRepository.findByEmail(userEmail.trim().toLowerCase())
                .ifPresent(refreshTokenService::revokeAllSessions);
        return "Logged out from all devices.";
    }

    @Transactional
    public String resendVerification(String rawEmail, String ip) {
        String email = rawEmail.trim().toLowerCase();

        if (userRepository.findByEmail(email).isPresent()) {
            return "If this email is pending verification, a new link has been sent.";
        }

        PendingRegistration pending = pendingRegistrationRepository
                .findByEmail(email)
                .orElseThrow(() -> new InvalidTokenException(
                        "If this email is pending verification, a new link has been sent ."));
        rateLimiterService.checkAndRecordEmail(email, ip);



        String rawToken = UUID.randomUUID().toString();
        String hashedToken =refreshTokenService.hashToken(rawToken);

        pending.setToken(hashedToken);
        pending.setExpiresAt(Instant.now().plusSeconds(VERIFICATION_TTL_SECONDS));
        pendingRegistrationRepository.save(pending);

        emailService.sendEmailVerification(email, rawToken);

        return "If this email is pending verification, a new link has been sent.";
    }


}
