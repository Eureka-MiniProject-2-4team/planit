package com.eureka.mp2.team4.planit.todo.personal.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PersonalTodoRequestDto {
    private String id;
    private String userId;
    private String title;
    private String content;
    private LocalDateTime targetDate;
    private Boolean isCompleted;
}