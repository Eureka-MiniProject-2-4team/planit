package com.eureka.mp2.team4.planit.friend.controller;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.friend.FriendStatus;
import com.eureka.mp2.team4.planit.friend.dto.FriendDto;
import com.eureka.mp2.team4.planit.friend.dto.request.FriendAskDto;
import com.eureka.mp2.team4.planit.friend.dto.request.FriendUpdateStatusDto;
import com.eureka.mp2.team4.planit.friend.dto.response.FriendListResponseDto;
import com.eureka.mp2.team4.planit.friend.service.FriendService;
import com.eureka.mp2.team4.planit.team.WithMockPlanitUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FriendController.class)
class FriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FriendService friendService;

    @Autowired
    private ObjectMapper objectMapper;

    private final String userId = "test-user-id";

    @Test
    @WithMockPlanitUser
    @DisplayName("친구 요청 전송 성공")
    void sendFriendRequest_success() throws Exception {
        FriendAskDto dto = FriendAskDto.builder().receiverId("user-b").build();

        ApiResponse response = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("친구 요청 전송 완료")
                .build();

        when(friendService.sendRequest(eq(userId), eq("user-b"))).thenReturn(response);

        mockMvc.perform(post("/api/friend")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("친구 요청 전송 완료"));
    }

    @Test
    @WithMockPlanitUser
    @DisplayName("친구 목록 조회 성공")
    void getFriendList_success() throws Exception {
        List<FriendDto> list = List.of(
                FriendDto.builder().id("f1").requesterId(userId).receiverId("user-b").build()
        );

        FriendListResponseDto dto = FriendListResponseDto.builder().friends(list).build();

        when(friendService.getFriends(eq(userId)))
                .thenReturn(ApiResponse.<FriendListResponseDto>builder()
                        .result(Result.SUCCESS)
                        .data(dto)
                        .build());

        mockMvc.perform(get("/api/friend"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.friends[0].id").value("f1"));
    }

    @Test
    @WithMockPlanitUser
    @DisplayName("받은 친구 요청 조회 성공")
    void getReceivedRequests_success() throws Exception {
        List<FriendDto> list = List.of(
                FriendDto.builder().id("f2").requesterId("user-x").receiverId(userId).build()
        );

        FriendListResponseDto dto = FriendListResponseDto.builder().friends(list).build();

        when(friendService.getReceivedRequests(eq(userId)))
                .thenReturn(ApiResponse.<FriendListResponseDto>builder()
                        .result(Result.SUCCESS)
                        .data(dto)
                        .build());

        mockMvc.perform(get("/api/friend/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.friends[0].id").value("f2"));
    }

    @Test
    @WithMockPlanitUser
    @DisplayName("보낸 친구 요청 조회 성공")
    void getSentRequests_success() throws Exception {
        List<FriendDto> list = List.of(
                FriendDto.builder().id("f3").requesterId(userId).receiverId("user-y").build()
        );

        FriendListResponseDto dto = FriendListResponseDto.builder().friends(list).build();

        when(friendService.getSentRequests(eq(userId)))
                .thenReturn(ApiResponse.<FriendListResponseDto>builder()
                        .result(Result.SUCCESS)
                        .data(dto)
                        .build());

        mockMvc.perform(get("/api/friend/sent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.friends[0].id").value("f3"));
    }

    @Test
    @WithMockPlanitUser
    @DisplayName("친구 요청 수락 성공")
    void acceptFriendRequest_success() throws Exception {
        FriendUpdateStatusDto dto = FriendUpdateStatusDto.builder()
                .status(FriendStatus.ACCEPTED)
                .build();

        when(friendService.updateStatus(eq("f4"), any()))
                .thenReturn(ApiResponse.builder()
                        .result(Result.SUCCESS)
                        .message("요청 상태 업데이트 완료")
                        .build());

        mockMvc.perform(patch("/api/friend/f4")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청 상태 업데이트 완료"));
    }

    @Test
    @WithMockPlanitUser
    @DisplayName("친구 삭제 성공")
    void deleteFriend_success() throws Exception {
        when(friendService.delete(eq("f5")))
                .thenReturn(ApiResponse.builder()
                        .result(Result.SUCCESS)
                        .message("친구 삭제 완료")
                        .build());

        mockMvc.perform(delete("/api/friend/f5")
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("친구 삭제 완료"));
    }
}
