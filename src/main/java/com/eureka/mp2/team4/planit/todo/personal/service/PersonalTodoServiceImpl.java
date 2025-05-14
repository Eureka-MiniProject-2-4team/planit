package com.eureka.mp2.team4.planit.todo.personal.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.exception.DatabaseException;
import com.eureka.mp2.team4.planit.common.exception.ForbiddenException;
import com.eureka.mp2.team4.planit.common.exception.NotFoundException;
import com.eureka.mp2.team4.planit.todo.personal.dto.PersonalTodoDto;
import com.eureka.mp2.team4.planit.todo.personal.dto.request.PersonalTodoRequestDto;
import com.eureka.mp2.team4.planit.todo.personal.dto.response.PersonalTodoListResponseDto;
import com.eureka.mp2.team4.planit.todo.personal.mapper.PersonalTodoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonalTodoServiceImpl implements PersonalTodoService {

    private final PersonalTodoMapper mapper;

    @Override
    public ApiResponse create(String userId, PersonalTodoRequestDto request) {
        try {
            String id = UUID.randomUUID().toString();
            mapper.insert(id, userId, request);
            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message("개인 투두 생성 완료")
                    .build();
        } catch (DataAccessException e) {
            throw new DatabaseException("개인 투두 생성 중 DB 오류 발생");
        }
    }

    @Override
    public ApiResponse update(String userId, String todoId, PersonalTodoRequestDto request) {
        try {
            PersonalTodoDto todo = mapper.findById(todoId);
            if (todo == null) {
                throw new NotFoundException("해당 ID의 투두가 존재하지 않습니다.");
            }

            if (!todo.getUserId().equals(userId)) {
                throw new ForbiddenException();
            }

            mapper.update(todoId, request);
            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message("개인 투두 수정 완료")
                    .build();
        } catch (DataAccessException e) {
            throw new DatabaseException("개인 투두 수정 중 DB 오류 발생");
        }
    }

    @Override
    public ApiResponse delete(String userId, String todoId) {
        try {
            PersonalTodoDto todo = mapper.findById(todoId);
            if (todo == null) {
                throw new NotFoundException("해당 ID의 투두가 존재하지 않습니다.");
            }

            if (!todo.getUserId().equals(userId)) {
                throw new ForbiddenException();
            }

            mapper.delete(todoId);
            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message("개인 투두 삭제 완료")
                    .build();
        } catch (DataAccessException e) {
            throw new DatabaseException("개인 투두 삭제 중 DB 오류 발생");
        }
    }

    @Override
    public ApiResponse getById(String userId, String todoId) {
        try {
            PersonalTodoDto todo = mapper.findById(todoId);
            if (todo == null) {
                throw new NotFoundException("해당 ID의 투두가 존재하지 않습니다.");
            }

            if (!todo.getUserId().equals(userId)) {
                throw new ForbiddenException();
            }

            return ApiResponse.<PersonalTodoDto>builder()
                    .result(Result.SUCCESS)
                    .message("개인 투두 조회 완료")
                    .data(todo)
                    .build();
        } catch (DataAccessException e) {
            throw new DatabaseException("개인 투두 조회 중 DB 오류 발생");
        }
    }

    @Override
    public ApiResponse getAllByUser(String userId) {
        try {
            List<PersonalTodoDto> list = mapper.findAllByUserId(userId);
            return ApiResponse.<PersonalTodoListResponseDto>builder()
                    .result(Result.SUCCESS)
                    .message("개인 투두 전체 조회 완료")
                    .data(PersonalTodoListResponseDto.builder().personalTodosDto(list).build())
                    .build();
        } catch (DataAccessException e) {
            throw new DatabaseException("개인 투두 전체 조회 중 DB 오류 발생");
        }
    }

    @Override
    public ApiResponse getAllByFriend(String friendId) {
        try {
            List<PersonalTodoDto> list = mapper.findAllByUserId(friendId);
            return ApiResponse.<PersonalTodoListResponseDto>builder()
                    .result(Result.SUCCESS)
                    .message("친구 투두 전체 조회 완료")
                    .data(PersonalTodoListResponseDto.builder().personalTodosDto(list).build())
                    .build();
        } catch (DataAccessException e) {
            throw new DatabaseException("친구 투두 전체 조회 중 DB 오류 발생");
        }
    }

    @Override
    public ApiResponse getFriendTodoById(String friendId, String todoId) {
        try {
            PersonalTodoDto todo = mapper.findById(todoId);
            if (todo == null) {
                throw new NotFoundException("해당 ID의 투두가 존재하지 않습니다.");
            }

            if (!todo.getUserId().equals(friendId)) {
                throw new ForbiddenException();
            }

            return ApiResponse.<PersonalTodoDto>builder()
                    .result(Result.SUCCESS)
                    .message("친구 투두 개별 조회 완료")
                    .data(todo)
                    .build();
        } catch (DataAccessException e) {
            throw new DatabaseException("친구 투두 조회 중 DB 오류 발생");
        }
    }
}
