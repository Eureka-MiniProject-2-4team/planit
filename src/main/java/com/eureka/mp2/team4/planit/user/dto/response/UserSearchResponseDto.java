package com.eureka.mp2.team4.planit.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSearchResponseDto {
    private String id;
    private String email;
    private String nickName;
    private Boolean isMe;
}
