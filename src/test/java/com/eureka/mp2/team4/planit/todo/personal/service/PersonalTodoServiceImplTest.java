package com.eureka.mp2.team4.planit.todo.personal.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.exception.ForbiddenException;
import com.eureka.mp2.team4.planit.common.exception.NotFoundException;
import com.eureka.mp2.team4.planit.todo.personal.dto.response.PersonalTodoListResponseDto;
import com.eureka.mp2.team4.planit.todo.personal.dto.request.PersonalTodoRequestDto;
import com.eureka.mp2.team4.planit.todo.personal.dto.PersonalTodoDto;
import com.eureka.mp2.team4.planit.todo.personal.mapper.PersonalTodoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PersonalTodoServiceImplTest {

    @Mock
    private PersonalTodoMapper mapper;

    @InjectMocks
    private PersonalTodoServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("투두 생성 성공")
    void create_success() {
        String userId = "user-1";
        PersonalTodoRequestDto request = PersonalTodoRequestDto.builder()
                .title("할 일")
                .content("내용")
                .targetDate(LocalDateTime.now())
                .build();

        ApiResponse response = service.create(userId, request);

        verify(mapper).insert(anyString(), eq(userId), eq(request));
        assertEquals(Result.SUCCESS, response.getResult());
    }

    @Test
    @DisplayName("투두 수정 성공")
    void update_success() {
        String userId = "user-1";
        String todoId = "todo-1";
        PersonalTodoDto dto = PersonalTodoDto.builder().userId(userId).build();
        PersonalTodoRequestDto request = PersonalTodoRequestDto.builder().title("변경").build();

        when(mapper.findById(todoId)).thenReturn(dto);

        ApiResponse response = service.update(userId, todoId, request);

        verify(mapper).update(todoId, request);
        assertEquals(Result.SUCCESS, response.getResult());
    }

    @Test
    @DisplayName("투두 수정 시 권한 없음")
    void update_forbidden() {
        String userId = "user-1";
        String todoId = "todo-1";
        PersonalTodoDto dto = PersonalTodoDto.builder().userId("other").build();
        PersonalTodoRequestDto request = PersonalTodoRequestDto.builder().build();

        when(mapper.findById(todoId)).thenReturn(dto);

        assertThrows(ForbiddenException.class, () -> service.update(userId, todoId, request));
    }

    @Test
    @DisplayName("투두 수정 시 존재하지 않음")
    void update_notFound() {
        String userId = "user-1";
        String todoId = "todo-x";
        PersonalTodoRequestDto request = PersonalTodoRequestDto.builder().build();

        when(mapper.findById(todoId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> service.update(userId, todoId, request));
    }

    @Test
    @DisplayName("투두 삭제 성공")
    void delete_success() {
        String userId = "user-1";
        String todoId = "todo-1";
        when(mapper.findById(todoId)).thenReturn(PersonalTodoDto.builder().userId(userId).build());

        ApiResponse response = service.delete(userId, todoId);

        verify(mapper).delete(todoId);
        assertEquals(Result.SUCCESS, response.getResult());
    }

    @Test
    @DisplayName("투두 삭제 시 권한 없음")
    void delete_forbidden() {
        String userId = "user-1";
        String todoId = "todo-1";
        when(mapper.findById(todoId)).thenReturn(PersonalTodoDto.builder().userId("other").build());

        assertThrows(ForbiddenException.class, () -> service.delete(userId, todoId));
    }

    @Test
    @DisplayName("투두 조회 성공")
    void getById_success() {
        String userId = "user-1";
        String todoId = "todo-1";
        PersonalTodoDto dto = PersonalTodoDto.builder().userId(userId).build();

        when(mapper.findById(todoId)).thenReturn(dto);

        ApiResponse response = service.getById(userId, todoId);

        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(dto, response.getData());
    }

    @Test
    @DisplayName("투두 조회 시 권한 없음")
    void getById_forbidden() {
        String userId = "user-1";
        String todoId = "todo-1";
        when(mapper.findById(todoId)).thenReturn(PersonalTodoDto.builder().userId("other").build());

        assertThrows(ForbiddenException.class, () -> service.getById(userId, todoId));
    }

    @Test
    @DisplayName("투두 조회 시 존재하지 않음")
    void getById_notFound() {
        String userId = "user-1";
        String todoId = "todo-x";
        when(mapper.findById(todoId)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> service.getById(userId, todoId));
    }

    @Test
    @DisplayName("투두 전체 조회 성공")
    void getAllByUser_success() {
        String userId = "user-1";
        List<PersonalTodoDto> todos = List.of(PersonalTodoDto.builder().build());
        when(mapper.findAllByUserId(userId)).thenReturn(todos);

        ApiResponse response = service.getAllByUser(userId);

        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(1, ((PersonalTodoListResponseDto) response.getData()).getPersonalTodosDto().size());
    }
}
