package com.eureka.mp2.team4.planit.friend.service;

import com.eureka.mp2.team4.planit.friend.FriendStatus;

public interface FriendQueryService {
    FriendStatus areFriends(String userId, String targetUserId);
}
