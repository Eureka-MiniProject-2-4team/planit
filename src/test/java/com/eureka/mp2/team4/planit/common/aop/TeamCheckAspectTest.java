package com.eureka.mp2.team4.planit.common.aop;

import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.common.annotations.TeamCheck;
import com.eureka.mp2.team4.planit.common.exception.ForbiddenException;
import com.eureka.mp2.team4.planit.common.exception.NotFoundException;
import com.eureka.mp2.team4.planit.team.service.TeamService;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;

import java.lang.reflect.Method;

import static com.eureka.mp2.team4.planit.team.constants.TeamMessages.NOT_FOUND_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TeamCheckAspectTest {

    private TeamCheckAspect aspect;
    private TeamService teamService;

    @BeforeEach
    void setup() {
        teamService = mock(TeamService.class);
        aspect = new TeamCheckAspect(teamService);

        // SecurityContext mocking
        PlanitUserDetails userDetails = mock(PlanitUserDetails.class);
        when(userDetails.getUsername()).thenReturn("test-user-id");

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);

        SecurityContextHolder.setContext(new SecurityContextImpl(auth));
    }

    @Test
    void 팀원이_아닐때_ForbiddenException() {
        // given
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"123e4567-e89b-12d3-a456-426614174000"});

        TeamCheck teamCheck = new TeamCheck() {
            @Override
            public boolean onlyLeader() {
                return false; // or true
            }

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return TeamCheck.class;
            }
        };
        when(teamService.isTeamMember(anyString(), anyString())).thenReturn(false);

        // then
        assertThrows(ForbiddenException.class, () -> aspect.checkTeamMembership(joinPoint, teamCheck));
    }

    @Test
    void 팀원은_맞지만_팀장이_아닐때_onlyLeader_true면_ForbiddenException() {
           JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"123e4567-e89b-12d3-a456-426614174000"});

        TeamCheck teamCheck = new TeamCheck() {
            @Override
            public boolean onlyLeader() {
                return true; // or true
            }

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return TeamCheck.class;
            }
        };
        when(teamService.isTeamMember(anyString(), anyString())).thenReturn(true);
        when(teamService.isTeamLeader(anyString(), anyString())).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> aspect.checkTeamMembership(joinPoint, teamCheck));
    }

    @Test
    void 팀장일_경우_정상_통과() {
        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"123e4567-e89b-12d3-a456-426614174000"});

        TeamCheck teamCheck = new TeamCheck() {
            @Override
            public boolean onlyLeader() {
                return true; // or true
            }

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return TeamCheck.class;
            }
        };
        when(teamService.isTeamMember(anyString(), anyString())).thenReturn(true);
        when(teamService.isTeamLeader(anyString(), anyString())).thenReturn(true);

        assertDoesNotThrow(() -> aspect.checkTeamMembership(joinPoint, teamCheck));
    }

    @Test
    void 유효한_UUID_없을때_NotFoundException_발생() {
        // given
        Object[] args = new Object[]{"not-a-uuid", 123, new Object()};

        // then
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> invokeExtractTeamId(args)
        );
        assertEquals(NOT_FOUND_ID, exception.getMessage());
    }

    private String invokeExtractTeamId(Object[] args) {
        try {
            Method method = TeamCheckAspect.class.getDeclaredMethod("extractTeamId", Object[].class);
            method.setAccessible(true);
            return (String) method.invoke(aspect, (Object) args);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof NotFoundException) {
                throw (NotFoundException) cause;
            }
            throw new RuntimeException(e);
        }
    }
}
