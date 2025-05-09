package com.eureka.mp2.team4.planit.todo.personal.mapper;

import com.eureka.mp2.team4.planit.todo.personal.dto.request.PersonalTodoRequestDto;
import com.eureka.mp2.team4.planit.todo.personal.dto.response.PersonalTodoResponseDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PersonalTodoMapper {
    void insert(PersonalTodoRequestDto request);

    void update(@Param("id") String id, @Param("request") PersonalTodoRequestDto request);

    void delete(@Param("id") String id);

    PersonalTodoResponseDto findById(@Param("id") String id);

    List<PersonalTodoResponseDto> findAllByUserId(@Param("userId") String userId);
}
