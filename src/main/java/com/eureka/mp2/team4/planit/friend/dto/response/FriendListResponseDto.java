package com.eureka.mp2.team4.planit.friend.dto.response;

import com.eureka.mp2.team4.planit.friend.dto.FriendDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendListResponseDto {
    private List<FriendDto> friends;
}