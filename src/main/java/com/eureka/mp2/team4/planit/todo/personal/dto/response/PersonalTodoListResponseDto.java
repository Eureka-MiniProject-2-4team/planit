package com.eureka.mp2.team4.planit.todo.personal.dto.response;

import com.eureka.mp2.team4.planit.todo.personal.dto.PersonalTodoDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonalTodoListResponseDto {
    private List<PersonalTodoDto> personalTodosDto;
}
