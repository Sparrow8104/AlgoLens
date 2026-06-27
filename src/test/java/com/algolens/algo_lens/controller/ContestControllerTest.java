package com.algolens.algo_lens.controller;

import com.algolens.algo_lens.dtos.contest.UpcomingContestDTO;
import com.algolens.algo_lens.services.service.ContestServices;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContestController.class)
@WithMockUser
public class ContestControllerTest extends BaseControllerTest {

    @MockitoBean
    private ContestServices contestServices;

    @MockitoBean
    private WebClient webClient;

    @Test
    public void upcomingContests_Success() throws Exception {
        when(contestServices.getUpcomingContests()).thenReturn(List.of(UpcomingContestDTO.builder().build()));

        mockMvc.perform(get("/api/contests/upcoming"))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllContests_Success() throws Exception {
        Page<UpcomingContestDTO> page = new PageImpl<>(List.of(UpcomingContestDTO.builder().build()));
        when(contestServices.getAllContestsPaginated(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/contests")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk());
    }
}
