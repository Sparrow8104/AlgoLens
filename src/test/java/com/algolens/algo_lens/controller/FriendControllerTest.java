package com.algolens.algo_lens.controller;

import com.algolens.algo_lens.dtos.friend.FriendRequestDTO;
import com.algolens.algo_lens.services.service.FriendServices;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FriendController.class)
@WithMockUser
public class FriendControllerTest extends BaseControllerTest {

    @MockitoBean
    private FriendServices friendServices;

    @Test
    public void addFriend_Success() throws Exception {
        doNothing().when(friendServices).addFriend(anyString(), anyString());

        String requestJson = """
                {
                    "userHandle": "jane",
                    "friendHandle": "john"
                }
                """;

        mockMvc.perform(post("/api/friends/add")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Friend added successfully"));
    }

    @Test
    public void removeFriend_Success() throws Exception {
        doNothing().when(friendServices).removeFriend("jane", "john");

        mockMvc.perform(delete("/api/friends/jane/remove/john")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Friend removed successfully"));
    }

    @Test
    public void getFriends_Success() throws Exception {
        when(friendServices.getFriends("jane")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/friends/jane"))
                .andExpect(status().isOk());
    }

    @Test
    public void getLeaderboard_Success() throws Exception {
        when(friendServices.getLeaderboard("jane")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/friends/jane/leaderboard"))
                .andExpect(status().isOk());
    }
}
