package com.eureka.mp2.team4.planit.todo.personal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalTodoResponseDto {
    private String id;
    private String userId;
    private String title;
    private String content;
    private LocalDateTime targetDate;
    private Boolean isCompleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}