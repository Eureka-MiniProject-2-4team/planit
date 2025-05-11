package com.eureka.mp2.team4.planit.friend.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.friend.FriendStatus;
import com.eureka.mp2.team4.planit.friend.constants.FriendMessages;
import com.eureka.mp2.team4.planit.friend.dto.FriendsDto;
import com.eureka.mp2.team4.planit.friend.dto.request.FriendAskDto;
import com.eureka.mp2.team4.planit.friend.dto.request.FriendUpdateStatusDto;
import com.eureka.mp2.team4.planit.friend.dto.response.FriendResponseDto;
import com.eureka.mp2.team4.planit.friend.mapper.FriendMapper;
import lombok.RequiredArgsConstructor;
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

        }catch (Exception e) {
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(FriendMessages.REQUEST_FAIL)
                    .build();
        }
    }

    @Override
    public ApiResponse<FriendsDto> getFriends(String userId) {
        try {
            List<FriendResponseDto> friends = mapper.findAllByUserId(userId);
            FriendsDto list = FriendsDto.builder().friends(friends).build();

            return ApiResponse.<FriendsDto>builder()
                    .result(Result.SUCCESS)
                    .message(FriendMessages.GET_FRIENDS_SUCCESS)
                    .data(list)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<FriendsDto>builder()
                    .result(Result.FAIL)
                    .message(FriendMessages.GET_FRIENDS_FAIL)
                    .build();
        }
    }

    @Override
    public ApiResponse<FriendsDto> getReceivedRequests(String userId) {
        try {
            List<FriendResponseDto> pending = mapper.findPendingByReceiverId(userId);
            FriendsDto list = FriendsDto.builder().friends(pending).build();

            return ApiResponse.<FriendsDto>builder()
                    .result(Result.SUCCESS)
                    .message(FriendMessages.GET_PENDING_SUCCESS)
                    .data(list)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<FriendsDto>builder()
                    .result(Result.FAIL)
                    .message(FriendMessages.GET_PENDING_FAIL)
                    .build();
        }
    }

    @Override
    public ApiResponse<FriendsDto> getSentRequests(String userId) {
        try {
            List<FriendResponseDto> sent = mapper.findAskedByRequesterId(userId);
            FriendsDto list = FriendsDto.builder().friends(sent).build();

            return ApiResponse.<FriendsDto>builder()
                    .result(Result.SUCCESS)
                    .message(FriendMessages.GET_SENT_SUCCESS)
                    .data(list)
                    .build();
        } catch (Exception e) {
            return ApiResponse.<FriendsDto>builder()
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
                FriendResponseDto acceptedRequest = mapper.findById(friendId);

                String oppositeRequesterId = acceptedRequest.getReceiverId();
                String oppositeReceiverId = acceptedRequest.getRequesterId();

                mapper.autoCancelOppositePending(oppositeRequesterId, oppositeReceiverId);
            }

            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message(FriendMessages.UPDATE_STATUS_SUCCESS)
                    .build();
        } catch (Exception e) {
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
        } catch (Exception e) {
            return ApiResponse.builder()
                    .result(Result.FAIL)
                    .message(FriendMessages.DELETE_FAIL)
                    .build();
        }
    }
}
