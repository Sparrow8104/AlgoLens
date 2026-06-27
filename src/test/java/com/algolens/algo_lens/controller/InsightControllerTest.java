package com.algolens.algo_lens.controller;

import com.algolens.algo_lens.dtos.insight.RecommendationDTO;
import com.algolens.algo_lens.dtos.insight.UpsolveDTO;
import com.algolens.algo_lens.dtos.insight.WeakTopicDTO;
import com.algolens.algo_lens.services.service.InsightServices;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InsightController.class)
@WithMockUser
public class InsightControllerTest extends BaseControllerTest {

    @MockitoBean
    private InsightServices insightServices;

    @Test
    public void getWeakTopics_Success() throws Exception {
        WeakTopicDTO topic = WeakTopicDTO.builder()
                .tag("dp")
                .totalAttempts(10)
                .solvedCount(3)
                .unsolvedCount(7)
                .acRate(0.3)
                .build();
        when(insightServices.getWeakTopics("tourist")).thenReturn(List.of(topic));

        mockMvc.perform(get("/api/insights/tourist/weak-topics"))
                .andExpect(status().isOk());
    }

    @Test
    public void getRecommendations_Success() throws Exception {
        RecommendationDTO recommendation = RecommendationDTO.builder()
                .contestId(123)
                .index("A")
                .name("Sample")
                .rating(1200)
                .tags(List.of("dp"))
                .build();
        when(insightServices.getRecommendations("tourist")).thenReturn(List.of(recommendation));

        mockMvc.perform(get("/api/insights/tourist/recommendations"))
                .andExpect(status().isOk());
    }

    @Test
    public void getUpsolveContests_Success() throws Exception {
        UpsolveDTO upsolve = UpsolveDTO.builder()
                .contestId(123)
                .index("A")
                .name("Sample")
                .rating(1200)
                .tags(List.of("dp"))
                .bestVerdict("WRONG_ANSWER")
                .url("http://codeforces.com")
                .build();
        Map<Integer, List<UpsolveDTO>> upsolveMap = Map.of(123, List.of(upsolve));
        when(insightServices.getUpsolveContests("tourist")).thenReturn(upsolveMap);

        mockMvc.perform(get("/api/insights/tourist/upsolve"))
                .andExpect(status().isOk());
    }
}
