package com.eureka.mp2.team4.planit.common.aop;

import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.common.annotations.FriendCheck;
import com.eureka.mp2.team4.planit.common.exception.ForbiddenException;
import com.eureka.mp2.team4.planit.common.exception.NotFoundException;
import com.eureka.mp2.team4.planit.friend.FriendParamType;
import com.eureka.mp2.team4.planit.friend.FriendRole;
import com.eureka.mp2.team4.planit.friend.FriendStatus;
import com.eureka.mp2.team4.planit.friend.dto.FriendDto;
import com.eureka.mp2.team4.planit.friend.service.FriendService;
import com.eureka.mp2.team4.planit.user.dto.UserDto;
import com.eureka.mp2.team4.planit.user.enums.UserRole;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class FriendCheckAspectTest {

    @Mock
    FriendService friendService;

    @Mock
    JoinPoint joinPoint;

    @InjectMocks
    FriendCheckAspect aspect;

    private FriendCheck friendCheck(FriendParamType type, FriendRole role, FriendStatus status) {
        return new FriendCheck() {
            public FriendParamType paramType() { return type; }
            public FriendRole mustBe() { return role; }
            public FriendStatus requiredStatus() { return status; }
            public Class<? extends Annotation> annotationType() { return FriendCheck.class; }
        };
    }

    private MockedStatic<SecurityContextHolder> mockSecurity(String userId) {
        Authentication auth = mock(Authentication.class);
        given(auth.getPrincipal()).willReturn(new PlanitUserDetails(
                new UserDto(userId, "email", "username","pw", "nick", UserRole.ROLE_USER,null,null,true,"01012345678")));

        SecurityContext context = mock(SecurityContext.class);
        given(context.getAuthentication()).willReturn(auth);

        MockedStatic<SecurityContextHolder> mock = mockStatic(SecurityContextHolder.class);
        mock.when(SecurityContextHolder::getContext).thenReturn(context);
        return mock;
    }

    @Test
    void FRIEND_ID_RECEIVER_PASS() {
        String userId = "user1", friendId = "11111111-1111-1111-1111-111111111111";
        FriendDto dto = FriendDto.builder()
                .receiverId(userId).requesterId("u2").status(FriendStatus.ACCEPTED).build();

        given(joinPoint.getArgs()).willReturn(new Object[]{friendId});
        given(friendService.findByFriendId(friendId)).willReturn(dto);

        try (MockedStatic<SecurityContextHolder> ignored = mockSecurity(userId)) {
            assertDoesNotThrow(() ->
                    aspect.checkTeamMembership(joinPoint, friendCheck(FriendParamType.FRIEND_ID, FriendRole.RECEIVER, FriendStatus.ACCEPTED)));
        }
    }

    @Test
    void FRIEND_ID_REQUESTER_FAIL() {
        String userId = "user1", friendId = "11111111-1111-1111-1111-111111111111";
        FriendDto dto = FriendDto.builder()
                .receiverId("other").requesterId("someoneElse").status(FriendStatus.ACCEPTED).build();

        given(joinPoint.getArgs()).willReturn(new Object[]{friendId});
        given(friendService.findByFriendId(friendId)).willReturn(dto);

        try (MockedStatic<SecurityContextHolder> ignored = mockSecurity(userId)) {
            assertThrows(ForbiddenException.class, () ->
                    aspect.checkTeamMembership(joinPoint, friendCheck(FriendParamType.FRIEND_ID, FriendRole.REQUESTER, FriendStatus.ACCEPTED)));
        }
    }

    @Test
    void FRIEND_ID_ANY_PASS() {
        String userId = "user1", friendId = "11111111-1111-1111-1111-111111111111";
        FriendDto dto = FriendDto.builder()
                .receiverId("someone").requesterId("other").status(FriendStatus.ACCEPTED).build();

        given(joinPoint.getArgs()).willReturn(new Object[]{friendId});
        given(friendService.findByFriendId(friendId)).willReturn(dto);

        try (MockedStatic<SecurityContextHolder> ignored = mockSecurity(userId)) {
            assertDoesNotThrow(() ->
                    aspect.checkTeamMembership(joinPoint, friendCheck(FriendParamType.FRIEND_ID, FriendRole.ANY, FriendStatus.ACCEPTED)));
        }
    }

    @Test
    void FRIEND_ID_STATUS_FAIL() {
        String userId = "user1", friendId = "11111111-1111-1111-1111-111111111111";
        FriendDto dto = FriendDto.builder()
                .receiverId(userId).requesterId("other").status(FriendStatus.PENDING).build();

        given(joinPoint.getArgs()).willReturn(new Object[]{friendId});
        given(friendService.findByFriendId(friendId)).willReturn(dto);

        try (MockedStatic<SecurityContextHolder> ignored = mockSecurity(userId)) {
            assertThrows(ForbiddenException.class, () ->
                    aspect.checkTeamMembership(joinPoint, friendCheck(FriendParamType.FRIEND_ID, FriendRole.RECEIVER, FriendStatus.ACCEPTED)));
        }
    }

    @Test
    void USER_ID_REQUESTER_PASS() {
        String userId = "user1", targetUserId = "11111111-1111-1111-1111-111111111111";
        FriendDto dto = FriendDto.builder()
                .requesterId(userId).receiverId(targetUserId).status(FriendStatus.ACCEPTED).build();

        given(joinPoint.getArgs()).willReturn(new Object[]{targetUserId});
        given(friendService.findByBothUserId(userId, targetUserId)).willReturn(dto);

        try (MockedStatic<SecurityContextHolder> ignored = mockSecurity(userId)) {
            assertDoesNotThrow(() ->
                    aspect.checkTeamMembership(joinPoint, friendCheck(FriendParamType.USER_ID, FriendRole.REQUESTER, FriendStatus.ACCEPTED)));
        }
    }

    @Test
    void USER_ID_RECEIVER_FAIL() {
        String userId = "user1", targetUserId = "11111111-1111-1111-1111-111111111111";
        FriendDto dto = FriendDto.builder()
                .requesterId("other").receiverId(targetUserId).status(FriendStatus.ACCEPTED).build();

        given(joinPoint.getArgs()).willReturn(new Object[]{targetUserId});
        given(friendService.findByBothUserId(userId, targetUserId)).willReturn(dto);

        try (MockedStatic<SecurityContextHolder> ignored = mockSecurity(userId)) {
            assertThrows(ForbiddenException.class, () ->
                    aspect.checkTeamMembership(joinPoint, friendCheck(FriendParamType.USER_ID, FriendRole.RECEIVER, FriendStatus.ACCEPTED)));
        }
    }

    @Test
    void USER_ID_ANY_PASS() {
        String userId = "user1", targetUserId = "11111111-1111-1111-1111-111111111111";
        FriendDto dto = FriendDto.builder()
                .requesterId("a").receiverId("b").status(FriendStatus.ACCEPTED).build();

        given(joinPoint.getArgs()).willReturn(new Object[]{targetUserId});
        given(friendService.findByBothUserId(userId, targetUserId)).willReturn(dto);

        try (MockedStatic<SecurityContextHolder> ignored = mockSecurity(userId)) {
            assertDoesNotThrow(() ->
                    aspect.checkTeamMembership(joinPoint, friendCheck(FriendParamType.USER_ID, FriendRole.ANY, FriendStatus.ACCEPTED)));
        }
    }

    @Test
    void ID_FORMAT_INVALID() {
        String userId = "user1", invalid = "not-a-uuid";

        given(joinPoint.getArgs()).willReturn(new Object[]{invalid});

        try (MockedStatic<SecurityContextHolder> ignored = mockSecurity(userId)) {
            assertThrows(NotFoundException.class, () ->
                    aspect.checkTeamMembership(joinPoint, friendCheck(FriendParamType.FRIEND_ID, FriendRole.ANY, FriendStatus.ACCEPTED)));
        }
    }
}