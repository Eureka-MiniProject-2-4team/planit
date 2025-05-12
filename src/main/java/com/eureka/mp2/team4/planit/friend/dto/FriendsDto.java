package com.eureka.mp2.team4.planit.friend.dto;

import com.eureka.mp2.team4.planit.friend.dto.response.FriendResponseDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendsDto {
    private List<FriendResponseDto> friends;
}