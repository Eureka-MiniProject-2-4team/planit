package com.eureka.mp2.team4.planit.team.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamRequestDto{
    private String id;
    private String teamName;
    private String description;
}
