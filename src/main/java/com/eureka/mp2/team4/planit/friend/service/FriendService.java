package com.eureka.mp2.team4.planit.friend.service;

import com.eureka.mp2.team4.planit.friend.dto.response.FriendListResponseDto;
import com.eureka.mp2.team4.planit.friend.dto.request.FriendAskDto;
import com.eureka.mp2.team4.planit.friend.dto.request.FriendUpdateStatusDto;
import com.eureka.mp2.team4.planit.common.ApiResponse;

public interface FriendService {

    ApiResponse sendRequest(FriendAskDto dto);

    ApiResponse<FriendListResponseDto> getFriends(String userId);

    ApiResponse<FriendListResponseDto> getReceivedRequests(String userId);

    ApiResponse<FriendListResponseDto> getSentRequests(String userId);

    ApiResponse updateStatus(String friendId, FriendUpdateStatusDto dto);

    ApiResponse delete(String friendId);
}
