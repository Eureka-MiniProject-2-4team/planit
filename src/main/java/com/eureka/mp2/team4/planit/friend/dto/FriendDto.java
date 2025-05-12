package com.eureka.mp2.team4.planit.friend.dto;

import com.eureka.mp2.team4.planit.friend.FriendStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendDto {
    private String id;
    private String requesterId;
    private String receiverId;
    private FriendStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime acceptedAt;
}
