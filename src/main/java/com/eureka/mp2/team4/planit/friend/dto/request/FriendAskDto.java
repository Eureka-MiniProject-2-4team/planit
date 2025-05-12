package com.eureka.mp2.team4.planit.friend.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendAskDto {
    private String id;
    private String requesterId;
    private String receiverId;
}
