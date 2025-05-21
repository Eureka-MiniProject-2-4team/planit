package com.eureka.mp2.team4.planit.user.dto.response;

import lombok.Getter;

@Getter
public class UserSearchForTeamResponseDto extends UserSearchResponseDto {
    private String teamMembershipStatus;

    public UserSearchForTeamResponseDto(String id, String email, String nickName, String teamMembershipStatus) {
        super(id, email, nickName, null);
        this.teamMembershipStatus = teamMembershipStatus;
    }
}
