package com.eureka.mp2.team4.planit.team;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockPlanitUserFactory.class)
public @interface WithMockPlanitUser {
    String username() default "test-user-id";
    String role() default "ROLE_USER";
}
