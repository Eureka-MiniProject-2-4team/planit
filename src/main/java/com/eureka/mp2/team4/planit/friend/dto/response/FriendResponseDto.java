package com.eureka.mp2.team4.planit.friend.dto.response;

import com.eureka.mp2.team4.planit.friend.FriendStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendResponseDto {
    private String id;
    private String requesterId;
    private String receiverId;
    private FriendStatus status;
}
