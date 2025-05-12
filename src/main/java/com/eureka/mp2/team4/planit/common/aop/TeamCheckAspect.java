package com.eureka.mp2.team4.planit.common.aop;

import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.common.annotations.TeamCheck;
import com.eureka.mp2.team4.planit.common.exception.ForbiddenException;
import com.eureka.mp2.team4.planit.common.exception.NotFoundException;
import com.eureka.mp2.team4.planit.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.eureka.mp2.team4.planit.team.constants.TeamMessages.NOT_FOUND_ID;


@Aspect
@Component
@RequiredArgsConstructor
public class TeamCheckAspect {
    private final TeamService teamService;

    @Before("@annotation(teamCheck)")
    public void checkTeamMembership(JoinPoint joinPoint, TeamCheck teamCheck) {
        Object[] args = joinPoint.getArgs();
        String teamId = extractTeamId(args); // teamId를 메서드 인자에서 추출 (로직은 아래에 따로 작성)
        String userId = getCurrentUserId();  // 로그인 사용자 ID 가져오기

        if (!teamService.isTeamMember(userId, teamId)) {
            throw new ForbiddenException();
        }

        if (teamCheck.onlyLeader() && !teamService.isTeamLeader(userId, teamId)) {
            throw new ForbiddenException();
        }
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PlanitUserDetails userDetails = (PlanitUserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    private String extractTeamId(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof String str && isUuidFormat(str)) {
                return str;
            }
        }
        throw new NotFoundException(NOT_FOUND_ID);
    }

    private boolean isUuidFormat(String s) {
        try {
            UUID.fromString(s);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
