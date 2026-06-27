package com.algolens.algo_lens.controller;

import com.algolens.algo_lens.dtos.ContestDTO;
import com.algolens.algo_lens.dtos.RatingGraphDTO;
import com.algolens.algo_lens.dtos.SubmissionStatsDTO;
import com.algolens.algo_lens.dtos.user.userInfo.UserProfileDTO;
import com.algolens.algo_lens.services.service.UserServices;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@WithMockUser
public class UserControllerTest extends BaseControllerTest {

    @MockitoBean
    private UserServices userServices;

    @Test
    public void getUserProfile_Success() throws Exception {
        UserProfileDTO profile = UserProfileDTO.builder()
                .handle("tourist")
                .rank("legendary grandmaster")
                .rating(3800)
                .build();
        when(userServices.getUserProfile("tourist")).thenReturn(profile);

        mockMvc.perform(get("/api/users/tourist/profile"))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserContestHistory_Success() throws Exception {
        when(userServices.getUserContestHistory("tourist")).thenReturn(List.of(ContestDTO.builder().build()));

        mockMvc.perform(get("/api/users/tourist/contest-history"))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserRatingGraph_Success() throws Exception {
        when(userServices.getUserRatingGraph("tourist")).thenReturn(List.of(RatingGraphDTO.builder().build()));

        mockMvc.perform(get("/api/users/tourist/rating-graph"))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserSubmissionStats_Success() throws Exception {
        when(userServices.getSubmissionStats("tourist")).thenReturn(SubmissionStatsDTO.builder().build());

        mockMvc.perform(get("/api/users/tourist/submission-stats"))
                .andExpect(status().isOk());
    }
}
