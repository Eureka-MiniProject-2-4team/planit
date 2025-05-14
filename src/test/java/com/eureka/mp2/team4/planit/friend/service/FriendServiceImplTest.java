package com.eureka.mp2.team4.planit.friend.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.friend.FriendStatus;
import com.eureka.mp2.team4.planit.friend.constants.FriendMessages;
import com.eureka.mp2.team4.planit.friend.dto.FriendDto;
import com.eureka.mp2.team4.planit.friend.dto.request.FriendUpdateStatusDto;
import com.eureka.mp2.team4.planit.friend.dto.response.FriendListResponseDto;
import com.eureka.mp2.team4.planit.friend.mapper.FriendMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessResourceFailureException;

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
        when(mapper.findByBothUserId("user1", "user2")).thenReturn(null);

        ApiResponse response = service.sendRequest("user1", "user2");

        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(FriendMessages.REQUEST_SUCCESS, response.getMessage());
        verify(mapper).insert(any());
    }

    @DisplayName("친구 요청 실패 - 이미 친구 상태")
    @Test
    void sendRequest_alreadyFriend() {
        when(mapper.findByBothUserId("user1", "user2")).thenReturn(
                FriendDto.builder().status(FriendStatus.ACCEPTED).build()
        );

        ApiResponse response = service.sendRequest("user1", "user2");

        assertEquals(Result.FAIL, response.getResult());
        assertEquals(FriendMessages.ALREADY_FRIEND, response.getMessage());
    }

    @DisplayName("친구 요청 실패 - 내가 이미 요청함")
    @Test
    void sendRequest_alreadySent() {
        when(mapper.findByBothUserId("user1", "user2")).thenReturn(
                FriendDto.builder()
                        .requesterId("user1")
                        .receiverId("user2")
                        .status(FriendStatus.PENDING)
                        .build()
        );

        ApiResponse response = service.sendRequest("user1", "user2");

        assertEquals(Result.FAIL, response.getResult());
        assertEquals(FriendMessages.ALREADY_SENT_REQUEST, response.getMessage());
    }

    @DisplayName("친구 요청 실패 - 상대가 나에게 요청한 상태")
    @Test
    void sendRequest_requestExistsFromReceiver() {
        when(mapper.findByBothUserId("user1", "user2")).thenReturn(
                FriendDto.builder()
                        .requesterId("user2")
                        .receiverId("user1")
                        .status(FriendStatus.PENDING)
                        .build()
        );

        ApiResponse response = service.sendRequest("user1", "user2");

        assertEquals(Result.FAIL, response.getResult());
        assertEquals(FriendMessages.REQUEST_EXISTS_FROM_RECEIVER, response.getMessage());
    }

    @DisplayName("친구 요청 실패 - DB 예외 발생")
    @Test
    void sendRequest_dbException() {
        when(mapper.findByBothUserId("user1", "user2")).thenReturn(null);
        doThrow(new DataAccessResourceFailureException("DB")).when(mapper).insert(any());

        ApiResponse response = service.sendRequest("user1", "user2");

        assertEquals(Result.FAIL, response.getResult());
        assertEquals(FriendMessages.REQUEST_FAIL, response.getMessage());
    }

    @DisplayName("친구 목록 조회 성공")
    @Test
    void getFriends_success() {
        when(mapper.findAllByUserId("user1")).thenReturn(List.of(
                FriendDto.builder().id("f1").requesterId("user1").receiverId("user2").build()
        ));

        ApiResponse<FriendListResponseDto> response = service.getFriends("user1");

        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(FriendMessages.GET_FRIENDS_SUCCESS, response.getMessage());
        assertEquals(1, response.getData().getFriends().size());
    }

    @DisplayName("친구 목록 조회 실패")
    @Test
    void getFriends_fail() {
        when(mapper.findAllByUserId("user1"))
                .thenThrow(new DataAccessResourceFailureException("DB 오류"));

        ApiResponse<FriendListResponseDto> response = service.getFriends("user1");

        assertEquals(Result.FAIL, response.getResult());
        assertEquals(FriendMessages.GET_FRIENDS_FAIL, response.getMessage());
    }

    @DisplayName("받은 친구 요청 조회 성공")
    @Test
    void getReceivedRequests_success() {
        when(mapper.findPendingByReceiverId("user2")).thenReturn(List.of(
                FriendDto.builder().id("f1").status(FriendStatus.PENDING).build()
        ));

        ApiResponse<FriendListResponseDto> response = service.getReceivedRequests("user2");

        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(FriendMessages.GET_PENDING_SUCCESS, response.getMessage());
        assertEquals(1, response.getData().getFriends().size());
    }

    @DisplayName("보낸 친구 요청 조회 실패")
    @Test
    void getSentRequests_fail() {
        when(mapper.findAskedByRequesterId("user1"))
                .thenThrow(new DataAccessResourceFailureException("DB 오류"));

        ApiResponse<FriendListResponseDto> response = service.getSentRequests("user1");

        assertEquals(Result.FAIL, response.getResult());
        assertEquals(FriendMessages.GET_SENT_FAIL, response.getMessage());
    }

    @DisplayName("친구 요청 상태 업데이트 성공")
    @Test
    void updateStatus_success() {
        FriendUpdateStatusDto dto = FriendUpdateStatusDto.builder()
                .status(FriendStatus.ACCEPTED)
                .build();

        FriendDto acceptedDto = FriendDto.builder()
                .requesterId("user1")
                .receiverId("user2")
                .build();

        when(mapper.findById("f1")).thenReturn(acceptedDto);

        ApiResponse response = service.updateStatus("f1", dto);

        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(FriendMessages.UPDATE_STATUS_SUCCESS, response.getMessage());
        verify(mapper).updateStatus("f1", "ACCEPTED");
        verify(mapper).autoCancelOppositePending("user2", "user1");
    }

    @DisplayName("친구 요청 상태 업데이트 실패")
    @Test
    void updateStatus_fail() {
        doThrow(new DataAccessResourceFailureException("DB 오류"))
                .when(mapper).updateStatus(any(), any());

        FriendUpdateStatusDto dto = FriendUpdateStatusDto.builder()
                .status(FriendStatus.REJECTED)
                .build();

        ApiResponse response = service.updateStatus("f1", dto);

        assertEquals(Result.FAIL, response.getResult());
        assertEquals(FriendMessages.UPDATE_STATUS_FAIL, response.getMessage());
    }

    @DisplayName("친구 삭제 성공")
    @Test
    void delete_success() {
        ApiResponse response = service.delete("f9");

        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(FriendMessages.DELETE_SUCCESS, response.getMessage());
        verify(mapper).delete("f9");
    }

    @DisplayName("친구 삭제 실패")
    @Test
    void delete_fail() {
        doThrow(new DataAccessResourceFailureException("DB 오류")).when(mapper).delete("f9");

        ApiResponse response = service.delete("f9");

        assertEquals(Result.FAIL, response.getResult());
        assertEquals(FriendMessages.DELETE_FAIL, response.getMessage());
    }

    @DisplayName("friendId로 친구 조회 - 성공")
    @Test
    void findByFriendId_success() {
        FriendDto mockFriend = FriendDto.builder()
                .id("f123")
                .requesterId("user1")
                .receiverId("user2")
                .status(FriendStatus.ACCEPTED)
                .build();

        when(mapper.findById("f123")).thenReturn(mockFriend);

        FriendDto result = service.findByFriendId("f123");

        assertNotNull(result);
        assertEquals("user1", result.getRequesterId());
        assertEquals("user2", result.getReceiverId());
        assertEquals(FriendStatus.ACCEPTED, result.getStatus());
    }

    @DisplayName("friendId로 친구 조회 - 없음")
    @Test
    void findByFriendId_notFound() {
        when(mapper.findById("not_exist")).thenReturn(null);

        FriendDto result = service.findByFriendId("not_exist");

        assertNull(result);
    }

    @DisplayName("두 userId로 친구 조회 - 성공")
    @Test
    void findByBothUserId_success() {
        FriendDto mockFriend = FriendDto.builder()
                .id("f456")
                .requesterId("user1")
                .receiverId("user2")
                .status(FriendStatus.PENDING)
                .build();

        when(mapper.findByBothUserId("user1", "user2")).thenReturn(mockFriend);

        FriendDto result = service.findByBothUserId("user1", "user2");

        assertNotNull(result);
        assertEquals("user1", result.getRequesterId());
        assertEquals("user2", result.getReceiverId());
        assertEquals(FriendStatus.PENDING, result.getStatus());
    }

    @DisplayName("두 userId로 친구 조회 - 없음")
    @Test
    void findByBothUserId_notFound() {
        when(mapper.findByBothUserId("user1", "user3")).thenReturn(null);

        FriendDto result = service.findByBothUserId("user1", "user3");

        assertNull(result);
    }
}
