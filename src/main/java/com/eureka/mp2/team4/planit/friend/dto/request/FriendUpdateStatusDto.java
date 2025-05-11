package com.eureka.mp2.team4.planit.friend.dto.request;

import com.eureka.mp2.team4.planit.friend.FriendStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendUpdateStatusDto {
    private FriendStatus status;
}
