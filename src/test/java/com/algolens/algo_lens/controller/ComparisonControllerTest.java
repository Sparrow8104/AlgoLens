package com.algolens.algo_lens.controller;

import com.algolens.algo_lens.dtos.comparison.RatingComparisonDTO;
import com.algolens.algo_lens.dtos.comparison.SubmissionCompareResponseDTO;
import com.algolens.algo_lens.services.service.ComparisonServices;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ComparisonController.class)
@WithMockUser
public class ComparisonControllerTest extends BaseControllerTest {

    @MockitoBean
    private ComparisonServices comparisonServices;

    @Test
    public void compareRatings_Success() throws Exception {
        RatingComparisonDTO mockDTO = RatingComparisonDTO.builder().build();
        when(comparisonServices.compareRatings("handle1", "handle2")).thenReturn(mockDTO);

        mockMvc.perform(get("/api/compare/rating")
                        .param("handle1", "handle1")
                        .param("handle2", "handle2"))
                .andExpect(status().isOk());
    }

    @Test
    public void findSubmissions_Success() throws Exception {
        SubmissionCompareResponseDTO mockDTO = SubmissionCompareResponseDTO.builder().build();
        when(comparisonServices.findSubmissions(any())).thenReturn(mockDTO);

        String requestJson = """
                {
                    "handle1": "user1",
                    "handle2": "user2"
                }
                """;

        mockMvc.perform(post("/api/compare/find")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
    }
}
