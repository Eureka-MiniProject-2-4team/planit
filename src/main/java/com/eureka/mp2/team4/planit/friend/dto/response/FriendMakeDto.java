package com.eureka.mp2.team4.planit.friend.dto.response;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FriendMakeDto {
    private String id;
    private String requesterId;
    private String receiverId;
}
