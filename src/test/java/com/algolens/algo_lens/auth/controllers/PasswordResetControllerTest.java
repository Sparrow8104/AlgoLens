package com.algolens.algo_lens.auth.controllers;

import com.algolens.algo_lens.auth.services.PasswordResetService;
import com.algolens.algo_lens.controller.BaseControllerTest;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PasswordResetController.class)
public class PasswordResetControllerTest extends BaseControllerTest {

    @MockitoBean
    private PasswordResetService passwordResetService;

    @Test
    public void forgotPassword_Success() throws Exception {
        when(passwordResetService.forgotPassword(any(), anyString()))
                .thenReturn("If that email is registered, a password reset link has been sent.");

        String requestJson = """
                {
                    "email": "jane@example.com"
                }
                """;

        mockMvc.perform(post("/api/auth/forgot-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("If that email is registered, a password reset link has been sent."));
    }

    @Test
    public void verifyResetToken_Success() throws Exception {
        doNothing().when(passwordResetService).verifyResetToken(any(), any());

        String requestJson = """
                {
                    "token": "valid-reset-token"
                }
                """;

        mockMvc.perform(post("/api/auth/verify-reset-token")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Verification code sent to your email."));
    }

    @Test
    public void resetPassword_Success() throws Exception {
        when(passwordResetService.resetPassword(any(), anyString(), any()))
                .thenReturn("Password reset successfully.");

        String requestJson = """
                {
                    "otp": "123456",
                    "newPassword": "NewSecurePassword123"
                }
                """;

        mockMvc.perform(post("/api/auth/reset-password")
                        .cookie(new Cookie("reset_session", "session-jti"))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Password reset successfully."));
    }
}
