package com.eureka.mp2.team4.planit.friend.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.friend.FriendStatus;
import com.eureka.mp2.team4.planit.friend.constants.FriendMessages;
import com.eureka.mp2.team4.planit.friend.dto.response.FriendListResponseDto;
import com.eureka.mp2.team4.planit.friend.dto.request.FriendAskDto;
import com.eureka.mp2.team4.planit.friend.dto.request.FriendUpdateStatusDto;
import com.eureka.mp2.team4.planit.friend.dto.FriendDto;
import com.eureka.mp2.team4.planit.friend.mapper.FriendMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService {

    private final FriendMapper mapper;

    @Override
    public ApiResponse sendRequest(FriendAskDto dto) {
        try {
            FriendAskDto friendAskDto = FriendAskDto.builder()
                    .id(UUID.randomUUID().toString())
                    .requesterId(dto.getRequesterId())
                    .receiverId(dto.getReceiverId())
                    .build();

            mapper.insert(friendAskDto);
            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(FriendMessages.REQUEST_SUCCESS)
                    .build();

        }catch (DuplicateKeyException e) {
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(FriendMessages.REQUEST_ALREADY_EXISTS)
                    .build();

        }catch (DataAccessException e) {
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(FriendMessages.REQUEST_FAIL)
                    .build();
        }
    }

    @Override
    public ApiResponse<FriendListResponseDto> getFriends(String userId) {
        try {
            List<FriendDto> friends = mapper.findAllByUserId(userId);
            FriendListResponseDto list = FriendListResponseDto.builder().friends(friends).build();

            return ApiResponse.<FriendListResponseDto>builder()
                    .result(Result.SUCCESS)
                    .message(FriendMessages.GET_FRIENDS_SUCCESS)
                    .data(list)
                    .build();
        } catch (DataAccessException e) {
            return ApiResponse.<FriendListResponseDto>builder()
                    .result(Result.FAIL)
                    .message(FriendMessages.GET_FRIENDS_FAIL)
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
                    .message(FriendMessages.GET_PENDING_SUCCESS)
                    .data(list)
                    .build();
        } catch (DataAccessException e) {
            return ApiResponse.<FriendListResponseDto>builder()
                    .result(Result.FAIL)
                    .message(FriendMessages.GET_PENDING_FAIL)
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
                    .message(FriendMessages.GET_SENT_SUCCESS)
                    .data(list)
                    .build();
        } catch (DataAccessException e) {
            return ApiResponse.<FriendListResponseDto>builder()
                    .result(Result.FAIL)
                    .message(FriendMessages.GET_SENT_FAIL)
                    .build();
        }
    }

    @Override
    public ApiResponse updateStatus(String friendId, FriendUpdateStatusDto dto) {
        try {
            mapper.updateStatus(friendId, dto.getStatus().name());

            if (dto.getStatus() == FriendStatus.ACCEPTED) {
                FriendDto acceptedRequest = mapper.findById(friendId);

                String oppositeRequesterId = acceptedRequest.getReceiverId();
                String oppositeReceiverId = acceptedRequest.getRequesterId();

                mapper.autoCancelOppositePending(oppositeRequesterId, oppositeReceiverId);
            }

            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(FriendMessages.UPDATE_STATUS_SUCCESS)
                    .build();
        } catch (DataAccessException e) {
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(FriendMessages.UPDATE_STATUS_FAIL)
                    .build();
        }
    }

    @Override
    public ApiResponse delete(String friendId) {
        try {
            mapper.delete(friendId);
            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(FriendMessages.DELETE_SUCCESS)
                    .build();
        } catch (DataAccessException e) {
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(FriendMessages.DELETE_FAIL)
                    .build();
        }
    }

    @Override
    public FriendDto findByFriendId(String friendId) {
        // todo : friendId로 FriendDto 찾아서 반환해주기
        return mapper.findById(friendId);
    }

    @Override
    public FriendDto findByBothUserId(String userId, String targetUserId) {
        /*
         todo : 두명의 아이디가 속한 FriendDto 찾아서 반환하기 -> 두 아이디의 위치가 바뀌어도 동일한 값이 나와야된다
         */
        return mapper.findByBothUserId(userId, targetUserId);
    }
}
