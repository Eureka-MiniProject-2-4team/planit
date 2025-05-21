package com.eureka.mp2.team4.planit.user.dto.response;

import com.eureka.mp2.team4.planit.friend.FriendStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserSearchForFriendResponseDto extends UserSearchResponseDto{
    private FriendStatus friendStatus;

}
