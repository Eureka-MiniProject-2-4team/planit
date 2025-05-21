package com.eureka.mp2.team4.planit.user.dto.response;

import com.eureka.mp2.team4.planit.friend.FriendStatus;
import lombok.Getter;

@Getter
public class UserSearchForFriendResponseDto extends UserSearchResponseDto {
    private FriendStatus friendStatus;

    public UserSearchForFriendResponseDto(String id, String email, String nickName, FriendStatus friendStatus) {
        super(id, email, nickName, null);
        this.friendStatus = friendStatus;
    }

}
