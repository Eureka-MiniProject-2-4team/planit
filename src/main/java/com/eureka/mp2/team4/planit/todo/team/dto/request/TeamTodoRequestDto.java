package com.eureka.mp2.team4.planit.todo.team.dto.request;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class TeamTodoRequestDto {
    private String id;
    private String teamId;
    private String title;
    private String content;
    private boolean isCompleted;
    private Timestamp createAt;
    private Timestamp updateAt;
}
