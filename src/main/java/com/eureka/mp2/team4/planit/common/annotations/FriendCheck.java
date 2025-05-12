package com.eureka.mp2.team4.planit.common.annotations;

import com.eureka.mp2.team4.planit.friend.FriendParamType;
import com.eureka.mp2.team4.planit.friend.FriendRole;
import com.eureka.mp2.team4.planit.friend.FriendStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FriendCheck {
    FriendParamType paramType();       // USER_ID or FRIEND_ID
    FriendRole mustBe() default FriendRole.ANY; // 로그인 유저의 역할
    FriendStatus requiredStatus() default FriendStatus.ACCEPTED;
}
