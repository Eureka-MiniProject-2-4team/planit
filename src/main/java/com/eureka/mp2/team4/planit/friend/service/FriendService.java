package com.eureka.mp2.team4.planit.friend.service;

import com.eureka.mp2.team4.planit.friend.dto.FriendsDto;
import com.eureka.mp2.team4.planit.friend.dto.request.FriendAskDto;
import com.eureka.mp2.team4.planit.friend.dto.request.FriendUpdateStatusDto;
import com.eureka.mp2.team4.planit.common.ApiResponse;

public interface FriendService {

    ApiResponse sendRequest(FriendAskDto dto);

    ApiResponse<FriendsDto> getFriends(String userId);

    ApiResponse<FriendsDto> getReceivedRequests(String userId);

    ApiResponse<FriendsDto> getSentRequests(String userId);

    ApiResponse updateStatus(String friendId, FriendUpdateStatusDto dto);

    ApiResponse delete(String friendId);
}
