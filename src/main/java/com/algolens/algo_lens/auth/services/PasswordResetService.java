package com.algolens.algo_lens.auth.services;

import com.algolens.algo_lens.auth.entities.PasswordResetToken;
import com.algolens.algo_lens.auth.entities.User;
import com.algolens.algo_lens.auth.exception.InvalidTokenException;
import com.algolens.algo_lens.auth.exception.TokenExpiredException;
import com.algolens.algo_lens.auth.repositories.PasswordResetTokenRepository;
import com.algolens.algo_lens.auth.repositories.RefreshTokenRepository;
import com.algolens.algo_lens.auth.repositories.UserRepository;
import com.algolens.algo_lens.auth.utils.ForgotPasswordRequest;
import com.algolens.algo_lens.auth.utils.ResetPasswordRequest;
import com.algolens.algo_lens.auth.utils.VerifyResetTokenRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.jsonwebtoken.SignatureAlgorithm;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class PasswordResetService {


    private static final long TOKEN_EXPIRY_SECONDS   = 900;
    private static final long TOKEN_EXPIRY_MINUTES   = TOKEN_EXPIRY_SECONDS / 60;
    private static final long OTP_EXPIRY_SECONDS     = 300;
    private static final long SESSION_COOKIE_SECONDS = 600;
    private static final int  OTP_MAX_ATTEMPTS       = 5;
    private static final int  OTP_DIGITS             = 6;

    private final UserRepository               userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final RefreshTokenRepository       refreshTokenRepository;
    private final PasswordEncoder              passwordEncoder;
    private final EmailService                 emailService;
    private final EmailRateLimiterService      emailRateLimiterService;

    @Value("${app.reset-token.secret}")
    private String jwtSecret;

    @Value("${app.reset-token.hmac-key}")
    private String hmacKey;

    public PasswordResetService(UserRepository userRepository,
                                PasswordResetTokenRepository passwordResetTokenRepository,
                                RefreshTokenRepository refreshTokenRepository,
                                PasswordEncoder passwordEncoder,
                                EmailService emailService,
                                EmailRateLimiterService emailRateLimiterService) {
        this.userRepository               = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.refreshTokenRepository       = refreshTokenRepository;
        this.passwordEncoder              = passwordEncoder;
        this.emailService                 = emailService;
        this.emailRateLimiterService      = emailRateLimiterService;
    }

    @Transactional
    public String forgotPassword(ForgotPasswordRequest request, String ip) {
        emailRateLimiterService.checkAndRecord(request.email(), ip);

        userRepository.findByEmail(request.email()).ifPresent(user -> {
            if (user.isEmailVerified()) {
                issueResetToken(user);
            }
        });

        return "If that email is registered, a password reset link has been sent.";
    }

    @Transactional(noRollbackFor = {InvalidTokenException.class, TokenExpiredException.class})
    public void verifyResetToken(VerifyResetTokenRequest request, HttpServletResponse response) {
        Claims claims = parseJwt(request.token());
        String jti    = claims.getId();
        String userId = claims.getSubject();

        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByJti(jti)
                .orElseThrow(() -> new InvalidTokenException("Invalid or expired reset token."));


        String expectedHash = hmacSha256(request.token());
        if (!expectedHash.equals(resetToken.getTokenHash())) {
            throw new InvalidTokenException("Token integrity check failed.");
        }


        if (!resetToken.getUser().getUserId().toString().equals(userId)) {
            throw new InvalidTokenException("Token does not belong to this account.");
        }

        if (resetToken.isUsed()) {
            passwordResetTokenRepository.delete(resetToken);
            throw new InvalidTokenException("This reset link has already been used.");
        }

        if (resetToken.getExpiresAt().isBefore(Instant.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new TokenExpiredException("Reset link has expired. Please request a new one.");
        }

        String otp     = generateOtp();
        Instant otpExp = Instant.now().plusSeconds(OTP_EXPIRY_SECONDS);

        resetToken.setOtpHash(passwordEncoder.encode(otp));
        resetToken.setOtpExpiresAt(otpExp);
        resetToken.setOtpAttempts(0);
        passwordResetTokenRepository.save(resetToken);

        emailService.sendOtpEmail(resetToken.getUser().getEmail(), otp, OTP_EXPIRY_SECONDS / 60);


        Cookie sessionCookie = new Cookie("reset_session", jti);
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(true);
        sessionCookie.setPath("/api/auth/reset-password");
        sessionCookie.setAttribute("SameSite", "Strict");
        sessionCookie.setMaxAge((int) SESSION_COOKIE_SECONDS);
        response.addCookie(sessionCookie);
    }

    @Transactional(noRollbackFor = {InvalidTokenException.class, TokenExpiredException.class})
    public String resetPassword(ResetPasswordRequest request,
                                String sessionJti,
                                HttpServletResponse response) {

        if (sessionJti == null || sessionJti.isBlank()) {
            throw new InvalidTokenException("Reset session missing or expired. Please start over.");
        }

        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByJti(sessionJti)
                .orElseThrow(() -> new InvalidTokenException("Reset session is invalid."));

        if (resetToken.isUsed()) {
            expireSessionCookie(response);
            passwordResetTokenRepository.delete(resetToken);
            throw new InvalidTokenException("This reset link has already been used.");
        }

        if (resetToken.getExpiresAt().isBefore(Instant.now())) {
            expireSessionCookie(response);
            passwordResetTokenRepository.delete(resetToken);
            throw new TokenExpiredException("Reset session has expired. Please request a new link.");
        }


        if (resetToken.getOtpAttempts() >= OTP_MAX_ATTEMPTS) {
            expireSessionCookie(response);
            passwordResetTokenRepository.delete(resetToken);
            throw new InvalidTokenException("Too many incorrect attempts. Please request a new reset link.");
        }

        if (resetToken.getOtpExpiresAt() == null ||
                resetToken.getOtpExpiresAt().isBefore(Instant.now())) {
            throw new InvalidTokenException("OTP has expired. Please go back and request a new code.");
        }


        resetToken.setOtpAttempts(resetToken.getOtpAttempts() + 1);
        passwordResetTokenRepository.save(resetToken);

        if (resetToken.getOtpHash() == null ||
                !passwordEncoder.matches(request.otp(), resetToken.getOtpHash())) {
            throw new InvalidTokenException("Invalid verification code.");
        }

        User user = resetToken.getUser();

        if (user.getPassword() != null &&
                passwordEncoder.matches(request.newPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from your current password.");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        resetToken.setOtpHash(null); // wipe immediately — no reason to keep it
        passwordResetTokenRepository.save(resetToken);


        refreshTokenRepository.deleteByUser(user);

        expireSessionCookie(response);

        return "Password reset successfully. Please log in with your new password.";
    }


    private void issueResetToken(User user) {
        String jti     = UUID.randomUUID().toString();
        Instant expiry = Instant.now().plusSeconds(TOKEN_EXPIRY_SECONDS);

        Key key         = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        String rawToken = Jwts.builder()
                .setSubject(user.getUserId().toString())
                .setId(jti)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(expiry))
                .claim("purpose", "password_reset")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String tokenHash = hmacSha256(rawToken);

        PasswordResetToken resetToken = passwordResetTokenRepository
                .findByUser(user)
                .map(existing -> {
                    existing.setJti(jti);
                    existing.setTokenHash(tokenHash);
                    existing.setExpiresAt(expiry);
                    existing.setUsed(false);
                    existing.setOtpHash(null);
                    existing.setOtpExpiresAt(null);
                    existing.setOtpAttempts(0);
                    return existing;
                })
                .orElseGet(() -> PasswordResetToken.builder()
                        .jti(jti)
                        .tokenHash(tokenHash)
                        .user(user)
                        .expiresAt(expiry)
                        .used(false)
                        .otpAttempts(0)
                        .build());

        passwordResetTokenRepository.save(resetToken);
        emailService.sendPasswordResetEmail(user.getEmail(), rawToken, TOKEN_EXPIRY_MINUTES);
    }

    private Claims parseJwt(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (!"password_reset".equals(claims.get("purpose", String.class))) {
                throw new InvalidTokenException("Token was not issued for password reset.");
            }
            return claims;

        } catch (JwtException e) {
            throw new InvalidTokenException("Invalid or expired reset token.");
        }
    }
    private String generateOtp() {
        SecureRandom rng = new SecureRandom();
        int otp = rng.nextInt((int) Math.pow(10, OTP_DIGITS));
        return String.format("%0" + OTP_DIGITS + "d", otp);
    }

    private String hmacSha256(String input) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(hmacKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new IllegalStateException("HMAC-SHA256 unavailable", e);
        }
    }

    private void expireSessionCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie("reset_session", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/auth/reset-password");
        cookie.setAttribute("SameSite", "Strict");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}