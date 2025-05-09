package com.eureka.mp2.team4.planit.todo.personal.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.todo.personal.dto.request.PersonalTodoRequestDto;

public interface PersonalTodoService {
    ApiResponse create(PersonalTodoRequestDto request);
    ApiResponse update(String id, PersonalTodoRequestDto request);
    ApiResponse delete(String id);
    ApiResponse getById(String id);
    ApiResponse getAllByUser(String userId);
}
