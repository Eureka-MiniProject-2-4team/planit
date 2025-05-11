package com.eureka.mp2.team4.planit.friend.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.friend.FriendStatus;
import com.eureka.mp2.team4.planit.friend.dto.FriendsDto;
import com.eureka.mp2.team4.planit.friend.dto.request.FriendAskDto;
import com.eureka.mp2.team4.planit.friend.dto.request.FriendUpdateStatusDto;
import com.eureka.mp2.team4.planit.friend.dto.response.FriendResponseDto;
import com.eureka.mp2.team4.planit.friend.mapper.FriendMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FriendServiceImplTest {

    @Mock
    private FriendMapper mapper;

    @InjectMocks
    private FriendServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("친구 요청 전송 성공")
    @Test
    void sendRequest_success() {
        FriendAskDto dto = FriendAskDto.builder()
                .requesterId("user1")
                .receiverId("user2")
                .build();

        ApiResponse response = service.sendRequest(dto);

        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals("친구 요청 전송 완료", response.getMessage());
        verify(mapper, times(1)).insert(any(FriendAskDto.class));
    }

    @DisplayName("친구 요청 전송 실패")
    @Test
    void sendRequest_fail() {
        doThrow(RuntimeException.class).when(mapper).insert(any());

        FriendAskDto dto = FriendAskDto.builder().requesterId("x").receiverId("y").build();
        ApiResponse response = service.sendRequest(dto);

        assertEquals(Result.FAIL, response.getResult());
        assertEquals("친구 요청 전송 실패", response.getMessage());
    }

    @DisplayName("친구 요청 중복 전송 실패")
    @Test
    void sendRequest_duplicate() {
        // given
        FriendAskDto dto = FriendAskDto.builder()
                .requesterId("user1")
                .receiverId("user2")
                .build();

        // when
        doThrow(DuplicateKeyException.class).when(mapper).insert(any(FriendAskDto.class));
        ApiResponse response = service.sendRequest(dto);

        // then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals("이미 친구 요청이 존재합니다.", response.getMessage());
    }

    @DisplayName("친구 목록 조회 성공")
    @Test
    void getFriends_success() {
        when(mapper.findAllByUserId("user1")).thenReturn(List.of(
                FriendResponseDto.builder().id("f1").requesterId("user1").receiverId("user2").build()
        ));

        ApiResponse<FriendsDto> response = service.getFriends("user1");

        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(1, response.getData().getFriends().size());
    }

    @DisplayName("친구 목록 조회 실패")
    @Test
    void getFriends_fail() {
        when(mapper.findAllByUserId("user1")).thenThrow(new RuntimeException());

        ApiResponse<FriendsDto> response = service.getFriends("user1");

        assertEquals(Result.FAIL, response.getResult());
        assertEquals("친구 목록 조회 실패", response.getMessage());
    }

    @DisplayName("받은 친구 요청 조회 성공")
    @Test
    void getReceivedRequests_success() {
        when(mapper.findPendingByReceiverId("user2")).thenReturn(List.of(
                FriendResponseDto.builder().id("f1").status(FriendStatus.PENDING).build()
        ));

        ApiResponse<FriendsDto> response = service.getReceivedRequests("user2");

        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(1, response.getData().getFriends().size());
    }

    @DisplayName("보낸 친구 요청 조회 실패")
    @Test
    void getSentRequests_fail() {
        when(mapper.findAskedByRequesterId("user1")).thenThrow(RuntimeException.class);

        ApiResponse<FriendsDto> response = service.getSentRequests("user1");

        assertEquals(Result.FAIL, response.getResult());
        assertEquals("보낸 요청 목록 조회 실패", response.getMessage());
    }

    @DisplayName("친구 요청 상태 업데이트 성공")
    @Test
    void updateStatus_success() {
        // given
        FriendUpdateStatusDto dto = FriendUpdateStatusDto.builder()
                .status(FriendStatus.ACCEPTED)
                .build();

        FriendResponseDto acceptedDto = FriendResponseDto.builder()
                .requesterId("user1")
                .receiverId("user2")
                .build();

        when(mapper.findById("f1")).thenReturn(acceptedDto);

        // when
        ApiResponse response = service.updateStatus("f1", dto);

        // then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals("요청 상태 업데이트 완료", response.getMessage());
        verify(mapper).updateStatus("f1", "ACCEPTED");
        verify(mapper).autoCancelOppositePending("user2", "user1");
    }

    @DisplayName("친구 요청 상태 업데이트 실패")
    @Test
    void updateStatus_fail() {
        doThrow(RuntimeException.class).when(mapper).updateStatus(any(), any());

        FriendUpdateStatusDto dto = FriendUpdateStatusDto.builder()
                .status(FriendStatus.REJECTED)
                .build();

        ApiResponse response = service.updateStatus("f1", dto);

        assertEquals(Result.FAIL, response.getResult());
        assertEquals("요청 상태 업데이트 실패", response.getMessage());
    }

    @DisplayName("친구 삭제 성공")
    @Test
    void delete_success() {
        ApiResponse response = service.delete("f9");

        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals("친구 삭제 완료", response.getMessage());
        verify(mapper).delete("f9");
    }

    @DisplayName("친구 삭제 실패")
    @Test
    void delete_fail() {
        doThrow(RuntimeException.class).when(mapper).delete("f9");

        ApiResponse response = service.delete("f9");

        assertEquals(Result.FAIL, response.getResult());
        assertEquals("친구 삭제 실패", response.getMessage());
    }
}
