package com.eureka.mp2.team4.planit.team.service;

import com.eureka.mp2.team4.planit.team.dto.TeamDto;
import com.eureka.mp2.team4.planit.team.dto.UserTeamDto;
import com.eureka.mp2.team4.planit.team.mapper.UserTeamMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserTeamQueryServiceImpl implements UserTeamQueryService {
    private final UserTeamMapper userTeamMapper;

    @Override
    public boolean isUserTeamLeader(String userId) {
        List<TeamDto> teams = userTeamMapper.findTeamByLeaderId(userId);
        return !teams.isEmpty();
    }

    @Override
    public String getTeamMemberShipStatus(String teamId, String id) {
        UserTeamDto userTeamDto = userTeamMapper.findByTeamIdAndUserId(teamId, id);
        if (userTeamDto == null)
            return null;

        return userTeamDto.getStatus();
    }
}
