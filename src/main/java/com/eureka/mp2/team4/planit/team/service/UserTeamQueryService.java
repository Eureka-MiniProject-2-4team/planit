package com.eureka.mp2.team4.planit.team.service;

public interface UserTeamQueryService {
    boolean isUserTeamLeader(String userId);

    String getTeamMemberShipStatus(String teamId, String id);
}
