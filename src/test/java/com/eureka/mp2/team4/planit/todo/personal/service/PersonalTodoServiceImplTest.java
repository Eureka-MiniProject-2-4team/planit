package com.eureka.mp2.team4.planit.todo.personal.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.exception.NotFoundException;
import com.eureka.mp2.team4.planit.todo.personal.dto.request.PersonalTodoRequestDto;
import com.eureka.mp2.team4.planit.todo.personal.dto.response.PersonalTodoResponseDto;
import com.eureka.mp2.team4.planit.todo.personal.mapper.PersonalTodoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
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
    void createTest() {
        PersonalTodoRequestDto request = PersonalTodoRequestDto.builder()
                .userId("user-1")
                .title("Test Todo")
                .content("content")
                .targetDate(LocalDateTime.now())
                .build();

        ApiResponse response = service.create(request);

        verify(mapper, times(1)).insert(any());
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals("개인 투두 생성 완료", response.getMessage());
    }

    @Test
    void updateTest_success() {
        String id = "todo-1";
        PersonalTodoRequestDto request = PersonalTodoRequestDto.builder()
                .title("Updated Title")
                .content("Updated content")
                .targetDate(LocalDateTime.now())
                .isCompleted(true)
                .build();

        when(mapper.findById(id)).thenReturn(new PersonalTodoResponseDto());

        ApiResponse response = service.update(id, request);

        verify(mapper).update(id, request);
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals("개인 투두 수정 완료", response.getMessage());
    }

    @Test
    void updateTest_notFound() {
        String id = "todo-404";
        PersonalTodoRequestDto request = PersonalTodoRequestDto.builder().build();

        when(mapper.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> service.update(id, request));
    }

    @Test
    void deleteTest_success() {
        String id = "todo-1";
        when(mapper.findById(id)).thenReturn(new PersonalTodoResponseDto());

        ApiResponse response = service.delete(id);

        verify(mapper).delete(id);
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals("개인 투두 삭제 완료", response.getMessage());
    }

    @Test
    void deleteTest_notFound() {
        String id = "todo-404";

        when(mapper.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> service.delete(id));
    }

    @Test
    void getByIdTest_success() {
        String id = "todo-1";
        PersonalTodoResponseDto dummyDto = new PersonalTodoResponseDto();

        when(mapper.findById(id)).thenReturn(dummyDto);

        ApiResponse response = service.getById(id);

        verify(mapper).findById(id);
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals("개인 투두 조회 완료", response.getMessage());
        assertEquals(dummyDto, response.getData());
    }

    @Test
    void getByIdTest_notFound() {
        String id = "todo-404";

        when(mapper.findById(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> service.getById(id));
    }

    @Test
    void getAllByUserTest() {
        String userId = "user-1";
        List<PersonalTodoResponseDto> list = List.of(new PersonalTodoResponseDto());

        when(mapper.findAllByUserId(userId)).thenReturn(list);

        ApiResponse response = service.getAllByUser(userId);

        verify(mapper).findAllByUserId(userId);
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals("개인 투두 전체 조회 완료", response.getMessage());
        assertEquals(list, response.getData());
    }
}
