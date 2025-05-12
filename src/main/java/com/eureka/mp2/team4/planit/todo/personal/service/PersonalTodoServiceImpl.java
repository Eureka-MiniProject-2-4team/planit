package com.eureka.mp2.team4.planit.todo.personal.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.exception.DatabaseException;
import com.eureka.mp2.team4.planit.common.exception.NotFoundException;
import com.eureka.mp2.team4.planit.todo.personal.dto.response.PersonalTodoListResponseDto;
import com.eureka.mp2.team4.planit.todo.personal.dto.request.PersonalTodoRequestDto;
import com.eureka.mp2.team4.planit.todo.personal.dto.PersonalTodoDto;
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
    public ApiResponse create(PersonalTodoRequestDto request) {
        try {
            PersonalTodoRequestDto requestWithId = PersonalTodoRequestDto.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(request.getUserId())
                    .title(request.getTitle())
                    .content(request.getContent())
                    .targetDate(request.getTargetDate())
                    .isCompleted(false)
                    .build();

            mapper.insert(requestWithId);
            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message("개인 투두 생성 완료")
                    .build();
        } catch (DataAccessException e) {
            throw new DatabaseException("개인 투두 생성 중 DB 오류 발생");
        }
    }

    @Override
    public ApiResponse update(String id, PersonalTodoRequestDto request) {
        try {
            if (mapper.findById(id) == null) {
                throw new NotFoundException("해당 ID의 투두가 존재하지 않습니다.");
            }

            mapper.update(id, request);
            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message("개인 투두 수정 완료")
                    .build();
        } catch (DataAccessException e) {
            throw new DatabaseException("개인 투두 수정 중 DB 오류 발생");
        }
    }

    @Override
    public ApiResponse delete(String id) {
        try {
            if (mapper.findById(id) == null) {
                throw new NotFoundException("해당 ID의 투두가 존재하지 않습니다.");
            }

            mapper.delete(id);
            return ApiResponse.builder()
                    .result(Result.SUCCESS)
                    .message("개인 투두 삭제 완료")
                    .build();
        } catch (DataAccessException e) {
            throw new DatabaseException("개인 투두 삭제 중 DB 오류 발생");
        }
    }

    @Override
    public ApiResponse getById(String id) {
        try {
            PersonalTodoDto todo = mapper.findById(id);
            if (todo == null) {
                throw new NotFoundException("해당 ID의 투두가 존재하지 않습니다.");
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
            PersonalTodoListResponseDto build = PersonalTodoListResponseDto.builder().personalTodosDto(list).build();

            return ApiResponse.<PersonalTodoListResponseDto>builder()
                    .result(Result.SUCCESS)
                    .message("개인 투두 전체 조회 완료")
                    .data(build)
                    .build();
        } catch (DataAccessException e) {
            throw new DatabaseException("개인 투두 전체 조회 중 DB 오류 발생");
        }
    }
}
