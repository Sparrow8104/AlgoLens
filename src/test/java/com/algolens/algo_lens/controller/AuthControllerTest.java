package com.algolens.algo_lens.controller;

import com.algolens.algo_lens.auth.services.AuthService;
import com.algolens.algo_lens.auth.services.PasswordResetService;
import com.algolens.algo_lens.auth.utils.AuthResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest extends BaseControllerTest {

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private PasswordResetService passwordResetService;

    @Test
    public void register_Success() throws Exception {
        when(authService.register(any(), anyString())).thenReturn("Registration successful.");

        String requestJson = """
                {
                    "name": "Jane Doe",
                    "email": "jane@example.com",
                    "password": "Password123"
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Registration successful."));
    }

    @Test
    public void verifyEmail_Success() throws Exception {
        when(authService.verifyEmail(anyString())).thenReturn("Email verified successfully.");

        mockMvc.perform(get("/api/auth/verify-email")
                        .param("token", "dummy-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("Email verified successfully."));
    }

    @Test
    public void login_Success() throws Exception {
        AuthResponse response = AuthResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .build();
        when(authService.login(any(), anyString(), anyString(), anyString())).thenReturn(response);

        String requestJson = """
                {
                    "email": "jane@example.com",
                    "password": "Password123"
                }
                """;

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "jane@example.com")
    public void logoutAll_Success() throws Exception {
        when(authService.logoutAll("jane@example.com")).thenReturn("Logged out from all devices.");

        mockMvc.perform(post("/api/auth/logout-all")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out from all devices."));
    }
}
