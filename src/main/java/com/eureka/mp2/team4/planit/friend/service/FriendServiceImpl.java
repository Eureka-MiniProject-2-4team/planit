package com.eureka.mp2.team4.planit.friend.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.exception.NotFoundException;
import com.eureka.mp2.team4.planit.friend.FriendStatus;
import com.eureka.mp2.team4.planit.friend.dto.FriendDto;
import com.eureka.mp2.team4.planit.friend.dto.request.FriendUpdateStatusDto;
import com.eureka.mp2.team4.planit.friend.dto.response.FriendListResponseDto;
import com.eureka.mp2.team4.planit.friend.dto.response.FriendMakeDto;
import com.eureka.mp2.team4.planit.friend.mapper.FriendMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.eureka.mp2.team4.planit.friend.constants.FriendMessages.*;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendMapper mapper;

    @Override
    public ApiResponse sendRequest(String requesterId, String receiverId) {
        if (receiverId == null) {
            throw new NotFoundException(RECEIVER_NOT_FOUND);
        }

        FriendDto existing = mapper.findByBothUserId(requesterId, receiverId);
        if (existing != null) {
            FriendStatus status = existing.getStatus();

            if (status == FriendStatus.ACCEPTED) {
                return ApiResponse.builder()
                        .result(Result.FAIL)
                        .message(ALREADY_FRIEND)
                        .build();
            }

            if (existing.getRequesterId().equals(requesterId)) {
                return ApiResponse.builder()
                        .result(Result.FAIL)
                        .message(ALREADY_SENT_REQUEST)
                        .build();
            } else {
                return ApiResponse.builder()
                        .result(Result.FAIL)
                        .message(REQUEST_EXISTS_FROM_RECEIVER)
                        .build();
            }
        }

        try {
            FriendMakeDto friendMakeDto = FriendMakeDto.builder()
                    .id(UUID.randomUUID().toString())
                    .requesterId(requesterId)
                    .receiverId(receiverId)
                    .build();

            mapper.insert(friendMakeDto);

            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(REQUEST_SUCCESS)
                    .build();

        } catch (DataAccessException e) {
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(REQUEST_FAIL)
                    .build();
        }
    }
    @Override
    public ApiResponse<FriendListResponseDto> getFriends(String userId) {
        try {
            List<FriendDto> friends = mapper.findAllByUserId(userId);

            if (friends == null || friends.isEmpty()) {
                return ApiResponse.<FriendListResponseDto>builder()
                        .result(Result.FAIL)
                        .message("친구 목록이 비어있습니다.")
                        .build();
            }

            List<FriendDto> connectedFriends = friends.stream()
                    .filter(friend -> friend.getRequesterId().equals(userId) || friend.getReceiverId().equals(userId)) // 내가 포함된 친구 관계만 필터링
                    .map(friend -> {
                        if (friend.getRequesterId().equals(userId)) {
                            return new FriendDto(
                                    friend.getId(),
                                    friend.getRequesterId(),
                                    friend.getRequesterNickName(),
                                    friend.getRequesterEmail(),
                                    friend.getReceiverId(),
                                    friend.getReceiverNickName(),
                                    friend.getReceiverEmail(),
                                    friend.getStatus(),
                                    friend.getCreatedAt(),
                                    friend.getAcceptedAt()
                            );
                        } else {
                            return new FriendDto(
                                    friend.getId(),
                                    friend.getReceiverId(),
                                    friend.getReceiverNickName(),
                                    friend.getReceiverEmail(),
                                    friend.getRequesterId(),
                                    friend.getRequesterNickName(),
                                    friend.getRequesterEmail(),
                                    friend.getStatus(),
                                    friend.getCreatedAt(),
                                    friend.getAcceptedAt()
                            );
                        }
                    })
                    .collect(Collectors.toList());

            FriendListResponseDto list = FriendListResponseDto.builder()
                    .friends(connectedFriends)
                    .build();

            return ApiResponse.<FriendListResponseDto>builder()
                    .result(Result.SUCCESS)
                    .message(GET_FRIENDS_SUCCESS)
                    .data(list)
                    .build();

        } catch (DataAccessException e) {
            return ApiResponse.<FriendListResponseDto>builder()
                    .result(Result.FAIL)
                    .message("친구 목록을 조회하는 데 실패했습니다. 데이터베이스 오류가 발생했습니다.")
                    .build();
        }
    }

    @Override
    public ApiResponse<FriendListResponseDto> getReceivedRequests(String userId) {
        try {
            List<FriendDto> pending = mapper.findPendingByReceiverId(userId);
            FriendListResponseDto list = FriendListResponseDto.builder().friends(pending).build();

            return ApiResponse.<FriendListResponseDto>builder()
                    .result(Result.SUCCESS)
                    .message(GET_PENDING_SUCCESS)
                    .data(list)
                    .build();
        } catch (DataAccessException e) {
            return ApiResponse.<FriendListResponseDto>builder()
                    .result(Result.FAIL)
                    .message(GET_PENDING_FAIL)
                    .build();
        }
    }

    @Override
    public ApiResponse<FriendListResponseDto> getSentRequests(String userId) {
        try {
            List<FriendDto> sent = mapper.findAskedByRequesterId(userId);
            FriendListResponseDto list = FriendListResponseDto.builder().friends(sent).build();

            return ApiResponse.<FriendListResponseDto>builder()
                    .result(Result.SUCCESS)
                    .message(GET_SENT_SUCCESS)
                    .data(list)
                    .build();
        } catch (DataAccessException e) {
            return ApiResponse.<FriendListResponseDto>builder()
                    .result(Result.FAIL)
                    .message(GET_SENT_FAIL)
                    .build();
        }
    }

    @Override
    @Transactional
    public ApiResponse updateStatus(String friendId, FriendUpdateStatusDto dto) {
        try {
            // 친구 상태 변경
            mapper.updateStatus(friendId, dto.getStatus().name());

            // 친구 수락 시 상대방의 대기 중인 요청을 자동 취소
            if (dto.getStatus() == FriendStatus.ACCEPTED) {
                FriendDto acceptedRequest = mapper.findById(friendId);

                String oppositeRequesterId = acceptedRequest.getReceiverId();
                String oppositeReceiverId = acceptedRequest.getRequesterId();

                // 상대방의 대기 상태를 'AUTO_CANCELLED'로 변경
                mapper.autoCancelOppositePending(oppositeRequesterId, oppositeReceiverId);
            }

            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(UPDATE_STATUS_SUCCESS)
                    .build();
        } catch (DataAccessException e) {
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(UPDATE_STATUS_FAIL)
                    .build();
        }
    }

    @Override
    public ApiResponse delete(String friendId) {
        try {
            mapper.delete(friendId);
            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(DELETE_SUCCESS)
                    .build();
        } catch (DataAccessException e) {
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(DELETE_FAIL)
                    .build();
        }
    }

    @Override
    public FriendDto findByFriendId(String friendId) {
        return mapper.findById(friendId);
    }

    @Override
    public FriendDto findByBothUserId(String userId, String targetUserId) {
        return mapper.findByBothUserId(userId, targetUserId);
    }
}
