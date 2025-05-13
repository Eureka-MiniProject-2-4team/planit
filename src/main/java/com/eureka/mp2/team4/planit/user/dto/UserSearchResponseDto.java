package com.eureka.mp2.team4.planit.user.dto;

import com.eureka.mp2.team4.planit.friend.FriendStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSearchResponseDto {
    private String id;
    private String email;
    private String nickName;
    private FriendStatus friendStatus;
    private String teamMembershipStatus;
    private Boolean isMe;
}
