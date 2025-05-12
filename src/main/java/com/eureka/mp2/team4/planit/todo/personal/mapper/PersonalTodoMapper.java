package com.eureka.mp2.team4.planit.todo.personal.mapper;

import com.eureka.mp2.team4.planit.todo.personal.dto.request.PersonalTodoRequestDto;
import com.eureka.mp2.team4.planit.todo.personal.dto.PersonalTodoDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PersonalTodoMapper {
    void insert(PersonalTodoRequestDto request);

    void update(@Param("id") String id, @Param("request") PersonalTodoRequestDto request);

    void delete(@Param("id") String id);

    PersonalTodoDto findById(@Param("id") String id);

    List<PersonalTodoDto> findAllByUserId(@Param("userId") String userId);
}
