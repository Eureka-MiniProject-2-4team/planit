package com.eureka.mp2.team4.planit.common.aop;

import com.eureka.mp2.team4.planit.auth.security.PlanitUserDetails;
import com.eureka.mp2.team4.planit.common.annotations.FriendCheck;
import com.eureka.mp2.team4.planit.common.exception.ForbiddenException;
import com.eureka.mp2.team4.planit.common.exception.NotFoundException;
import com.eureka.mp2.team4.planit.friend.FriendParamType;
import com.eureka.mp2.team4.planit.friend.FriendRole;
import com.eureka.mp2.team4.planit.friend.dto.FriendDto;
import com.eureka.mp2.team4.planit.friend.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.eureka.mp2.team4.planit.friend.constants.FriendMessages.NOT_FOUND_ID;


@Aspect
@Component
@RequiredArgsConstructor
public class FriendCheckAspect {
    private final FriendService friendService;

    @Before("@annotation(friendCheck)")
    public void checkTeamMembership(JoinPoint joinPoint, FriendCheck friendCheck) {
        // 파라미터가 friend_id로 들어왔을 때
        // ex : @FriendCheck(paramType = FriendParamType.FRIEND_ID)
        Object[] args = joinPoint.getArgs();
        String userId = getCurrentUserId();
        FriendDto friendDto;

        if (friendCheck.paramType() == FriendParamType.FRIEND_ID) {
            String friendId = extractId(args);
            friendDto = friendService.findByFriendId(friendId);
        } else {
            String targetUserId = extractId(args);
            friendDto = friendService.findByBothUserId(userId, targetUserId);
        }

        validateMustBe(friendCheck, friendDto, userId);
        validateRequiredStatus(friendCheck, friendDto);
    }

    private void validateRequiredStatus(FriendCheck friendCheck, FriendDto friendDto) {
        if(friendDto.getStatus().equals(friendCheck.requiredStatus()))
            return;

        throw new ForbiddenException();
    }

    private static void validateMustBe(FriendCheck friendCheck, FriendDto friendDto, String userId) {
        FriendRole mustBe = friendCheck.mustBe();

        if (mustBe == FriendRole.RECEIVER && !friendDto.getReceiverId().equals(userId)) {
            throw new ForbiddenException();
        }

        if (mustBe == FriendRole.REQUESTER && !friendDto.getRequesterId().equals(userId)) {
            throw new ForbiddenException();
        }
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PlanitUserDetails userDetails = (PlanitUserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    private String extractId(Object[] args) {
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
