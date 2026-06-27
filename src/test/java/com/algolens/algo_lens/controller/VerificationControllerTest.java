package com.algolens.algo_lens.controller;

import com.algolens.algo_lens.auth.entities.User;
import com.algolens.algo_lens.auth.entities.UserRole;
import com.algolens.algo_lens.auth.services.RateLimiterService;
import com.algolens.algo_lens.services.VerificationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VerificationController.class)
public class VerificationControllerTest extends BaseControllerTest {

    @MockitoBean
    private VerificationService verificationService;

    @MockitoBean
    private RateLimiterService rateLimiterService;

    @Test
    public void sendOtp_Success() throws Exception {
        User mockUser = new User();
        mockUser.setEmail("jane@example.com");
        mockUser.setRole(UserRole.USER);

        doNothing().when(rateLimiterService).checkAndRecordPhone(anyString(), anyString());
        doNothing().when(verificationService).generateAndSendOtp(any(), anyString());

        String requestJson = """
                {
                    "phoneNumber": "+1234567890"
                }
                """;

        mockMvc.perform(post("/api/verification/send-otp")
                        .with(user(mockUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("OTP_SENT"));
    }

    @Test
    public void sendOtp_Unauthorized() throws Exception {
        String requestJson = """
                {
                    "phoneNumber": "+1234567890"
                }
                """;

        mockMvc.perform(post("/api/verification/send-otp")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isForbidden());
    }

    @Test
    public void verifyOtp_Success() throws Exception {
        User mockUser = new User();
        mockUser.setEmail("jane@example.com");
        mockUser.setRole(UserRole.USER);

        when(verificationService.verifyOtp(any(), anyString())).thenReturn(true);

        String requestJson = """
                {
                    "otp": "123456"
                }
                """;

        mockMvc.perform(post("/api/verification/verify-otp")
                        .with(user(mockUser))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("OTP_VERIFIED"));
    }
}
