package com.eureka.mp2.team4.planit.todo.personal.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.todo.personal.dto.request.PersonalTodoRequestDto;

public interface PersonalTodoService {
    ApiResponse create(String userId, PersonalTodoRequestDto request);
    ApiResponse update(String userId, String todoId, PersonalTodoRequestDto request);
    ApiResponse delete(String userId, String todoId);
    ApiResponse getById(String userId, String todoId);
    ApiResponse getAllByUser(String userId);
    ApiResponse getAllByFriend(String friendId);
    ApiResponse getFriendTodoById(String friendId, String todoId);
}
