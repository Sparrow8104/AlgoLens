package com.algolens.algo_lens.controller;

import com.algolens.algo_lens.dtos.analysis.AiAnalysisResponseDTO;
import com.algolens.algo_lens.services.service.AnalysisService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalysisController.class)
@WithMockUser
public class AnalysisControllerTest extends BaseControllerTest {

    @MockitoBean
    private AnalysisService aiAnalysisService;

    @Test
    public void analyzeUpsolve_Success() throws Exception {
        AiAnalysisResponseDTO response = new AiAnalysisResponseDTO(
                java.util.Collections.emptyList(),
                "Keep practicing tree problems"
        );
        when(aiAnalysisService.analyzeUpsolve("tourist")).thenReturn(response);

        mockMvc.perform(get("/api/analysis/upsolve/tourist"))
                .andExpect(status().isOk());
    }
}
