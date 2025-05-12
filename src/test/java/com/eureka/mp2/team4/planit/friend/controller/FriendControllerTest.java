package com.eureka.mp2.team4.planit.friend.controller;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.friend.dto.response.FriendListResponseDto;
import com.eureka.mp2.team4.planit.friend.dto.request.FriendAskDto;
import com.eureka.mp2.team4.planit.friend.dto.request.FriendUpdateStatusDto;
import com.eureka.mp2.team4.planit.friend.dto.FriendDto;
import com.eureka.mp2.team4.planit.friend.service.FriendService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FriendController.class)
@AutoConfigureMockMvc(addFilters = false)
class FriendControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FriendService friendService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("친구 요청 전송 성공")
    @Test
    void sendFriendRequest_success() throws Exception {
        FriendAskDto dto = FriendAskDto.builder()
                .requesterId("user-a")
                .receiverId("user-b")
                .build();

        ApiResponse response = ApiResponse.builder()
                .result(Result.SUCCESS)
                .message("친구 요청 전송 완료")
                .build();

        when(friendService.sendRequest(any())).thenReturn(response);

        mockMvc.perform(post("/api/friend")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("친구 요청 전송 완료"));
    }

    @DisplayName("친구 목록 조회 성공")
    @Test
    void getFriendList_success() throws Exception {
        List<FriendDto> list = List.of(
                FriendDto.builder().id("f1").requesterId("user-a").receiverId("user-b").build()
        );

        FriendListResponseDto dto = FriendListResponseDto.builder().friends(list).build();

        when(friendService.getFriends("user-a"))
                .thenReturn(ApiResponse.<FriendListResponseDto>builder()
                        .result(Result.SUCCESS)
                        .data(dto)
                        .build());

        mockMvc.perform(get("/api/friend")
                        .param("userId", "user-a"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.friends[0].id").value("f1"));
    }

    @DisplayName("받은 친구 요청 조회 성공")
    @Test
    void getReceivedRequests_success() throws Exception {
        List<FriendDto> list = List.of(
                FriendDto.builder().id("f2").requesterId("user-x").receiverId("user-a").build()
        );

        FriendListResponseDto dto = FriendListResponseDto.builder().friends(list).build();

        when(friendService.getReceivedRequests("user-a"))
                .thenReturn(ApiResponse.<FriendListResponseDto>builder()
                        .result(Result.SUCCESS)
                        .data(dto)
                        .build());

        mockMvc.perform(get("/api/friend/pending")
                        .param("userId", "user-a"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.friends[0].id").value("f2"));
    }

    @DisplayName("보낸 친구 요청 조회 성공")
    @Test
    void getSentRequests_success() throws Exception {
        List<FriendDto> list = List.of(
                FriendDto.builder().id("f3").requesterId("user-a").receiverId("user-y").build()
        );

        FriendListResponseDto dto = FriendListResponseDto.builder().friends(list).build();

        when(friendService.getSentRequests("user-a"))
                .thenReturn(ApiResponse.<FriendListResponseDto>builder()
                        .result(Result.SUCCESS)
                        .data(dto)
                        .build());

        mockMvc.perform(get("/api/friend/sent")
                        .param("userId", "user-a"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.friends[0].id").value("f3"));
    }

    @DisplayName("친구 요청 수락 성공")
    @Test
    void acceptFriendRequest_success() throws Exception {
        FriendUpdateStatusDto dto = FriendUpdateStatusDto.builder()
                .status(com.eureka.mp2.team4.planit.friend.FriendStatus.ACCEPTED)
                .build();

        when(friendService.updateStatus(eq("f4"), any()))
                .thenReturn(ApiResponse.builder()
                        .result(Result.SUCCESS)
                        .message("요청 상태 업데이트 완료")
                        .build());

        mockMvc.perform(patch("/api/friend/f4")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("요청 상태 업데이트 완료"));
    }

    @DisplayName("친구 삭제 성공")
    @Test
    void deleteFriend_success() throws Exception {
        when(friendService.delete("f5"))
                .thenReturn(ApiResponse.builder()
                        .result(Result.SUCCESS)
                        .message("친구 삭제 완료")
                        .build());

        mockMvc.perform(delete("/api/friend/f5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("친구 삭제 완료"));
    }
}
