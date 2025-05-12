package com.eureka.mp2.team4.planit.todo.personal.dto;

import com.eureka.mp2.team4.planit.todo.personal.dto.response.PersonalTodoResponseDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalTodosDto {
    private List<PersonalTodoResponseDto> personalTodosDto;
}
