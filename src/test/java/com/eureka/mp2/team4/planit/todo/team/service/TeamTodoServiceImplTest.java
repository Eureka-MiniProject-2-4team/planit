package com.eureka.mp2.team4.planit.todo.team.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.eureka.mp2.team4.planit.todo.team.constants.TeamTodoMessages.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.todo.team.dto.TeamTodoDto;
import com.eureka.mp2.team4.planit.todo.team.dto.request.TeamTodoReqDto;
import com.eureka.mp2.team4.planit.todo.team.dto.response.TeamTodoListResDto;
import com.eureka.mp2.team4.planit.todo.team.dto.response.TeamTodoResDto;
import com.eureka.mp2.team4.planit.todo.team.mapper.TeamTodoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class TeamTodoServiceImplTest {

    @Mock
    private TeamTodoMapper teamTodoMapper;

    @InjectMocks
    private TeamTodoServiceImpl teamTodoService;

    private String teamId;
    private String teamTodoId;
    private TeamTodoDto teamTodoDto;
    private TeamTodoReqDto teamTodoReqDto;
    private List<TeamTodoDto> teamTodoDtoList;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        teamId = UUID.randomUUID().toString();
        teamTodoId = UUID.randomUUID().toString();

        // TeamTodoDto 설정
        teamTodoDto = TeamTodoDto.builder()
                .id(teamTodoId)
                .teamId(teamId)
                .title("테스트 할 일")
                .content("테스트 내용")
                .isCompleted(false)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        // TeamTodoReqDto 설정
        teamTodoReqDto = new TeamTodoReqDto();
        teamTodoReqDto.setId(teamTodoId);
        teamTodoReqDto.setTeamId(teamId);
        teamTodoReqDto.setTitle("테스트 할 일");
        teamTodoReqDto.setContent("테스트 내용");
        teamTodoReqDto.setCompleted(false);

        // TeamTodoDto 리스트 설정
        teamTodoDtoList = Arrays.asList(
                teamTodoDto,
                TeamTodoDto.builder()
                        .id(UUID.randomUUID().toString())
                        .teamId(teamId)
                        .title("두 번째 할 일")
                        .content("두 번째 내용")
                        .isCompleted(true)
                        .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                        .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
                        .build()
        );
    }

    // 1. createTeamTodo 테스트 - 실패 케이스
    @Test
    void testCreateTeamTodoFail() {
        // Given
        doThrow(new RuntimeException("DB 에러")).when(teamTodoMapper).createTeamTodo(any(TeamTodoDto.class));

        // When
        ApiResponse response = teamTodoService.createTeamTodo(teamTodoReqDto);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(REGISTER_TEAMTODO_FAIL, response.getMessage());
        assertNull(response.getData());
        verify(teamTodoMapper, times(1)).createTeamTodo(any(TeamTodoDto.class));
    }

    // 2. createTeamTodo 테스트 - 성공 케이스
    @Test
    void testCreateTeamTodoSuccess() {
        // Given
        doNothing().when(teamTodoMapper).createTeamTodo(any(TeamTodoDto.class));

        // When
        ApiResponse response = teamTodoService.createTeamTodo(teamTodoReqDto);

        // Then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(REGISTER_TEAMTODO_SUCCESS, response.getMessage());
        assertNull(response.getData());
        verify(teamTodoMapper, times(1)).createTeamTodo(any(TeamTodoDto.class));
    }

    // 3. getTeamTodoList 테스트 - 실패 케이스 (데이터 없음)
    @Test
    void testGetTeamTodoListFailNotFound() {
        // Given
        when(teamTodoMapper.getTeamTodoList(eq(teamId))).thenReturn(null);

        // When
        ApiResponse response = teamTodoService.getTeamTodoList(teamId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(GET_TEAMTODO_LIST_FAIL, response.getMessage());
        assertNull(response.getData());
        verify(teamTodoMapper, times(1)).getTeamTodoList(eq(teamId));
    }

    // 4. getTeamTodoList 테스트 - 실패 케이스 (예외 발생)
    @Test
    void testGetTeamTodoListFailException() {
        // Given
        when(teamTodoMapper.getTeamTodoList(eq(teamId))).thenThrow(new RuntimeException("DB 에러"));

        // When
        ApiResponse response = teamTodoService.getTeamTodoList(teamId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(GET_TEAMTODO_LIST_FAIL, response.getMessage());
        assertNull(response.getData());
        verify(teamTodoMapper, times(1)).getTeamTodoList(eq(teamId));
    }

    // 5. getTeamTodoList 테스트 - 성공 케이스
    @Test
    void testGetTeamTodoListSuccess() {
        // Given
        when(teamTodoMapper.getTeamTodoList(eq(teamId))).thenReturn(teamTodoDtoList);

        // When
        ApiResponse response = teamTodoService.getTeamTodoList(teamId);

        // Then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(GET_TEAMTODO_LIST_SUCCESS, response.getMessage());
        assertNotNull(response.getData());
        assertTrue(response.getData() instanceof TeamTodoListResDto);
        TeamTodoListResDto resDto = (TeamTodoListResDto) response.getData();
        assertEquals(2, resDto.getTeamTodoDtoList().size());
        verify(teamTodoMapper, times(2)).getTeamTodoList(eq(teamId));
    }

    // 6. getTeamTodoById 테스트 - 실패 케이스 (데이터 없음)
    @Test
    void testGetTeamTodoByIdFailNotFound() {
        // Given
        when(teamTodoMapper.getTeamTodoById(eq(teamTodoId))).thenReturn(null);

        // When
        ApiResponse response = teamTodoService.getTeamTodoById(teamTodoId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(GET_TEAMTODO_FAIL, response.getMessage());
        assertNull(response.getData());
        verify(teamTodoMapper, times(1)).getTeamTodoById(eq(teamTodoId));
    }

    // 7. getTeamTodoById 테스트 - 실패 케이스 (예외 발생)
    @Test
    void testGetTeamTodoByIdFailException() {
        // Given
        when(teamTodoMapper.getTeamTodoById(eq(teamTodoId))).thenThrow(new RuntimeException("DB 에러"));

        // When
        ApiResponse response = teamTodoService.getTeamTodoById(teamTodoId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(GET_TEAMTODO_FAIL, response.getMessage());
        assertNull(response.getData());
        verify(teamTodoMapper, times(1)).getTeamTodoById(eq(teamTodoId));
    }

    // 8. getTeamTodoById 테스트 - 성공 케이스
    @Test
    void testGetTeamTodoByIdSuccess() {
        // Given
        when(teamTodoMapper.getTeamTodoById(eq(teamTodoId))).thenReturn(teamTodoDto);

        // When
        ApiResponse response = teamTodoService.getTeamTodoById(teamTodoId);

        // Then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(GET_TEAMTODO_SUCCESS, response.getMessage());
        assertNotNull(response.getData());
        assertTrue(response.getData() instanceof TeamTodoResDto);
        TeamTodoResDto resDto = (TeamTodoResDto) response.getData();
        assertEquals("테스트 할 일", resDto.getTitle());
        assertEquals("테스트 내용", resDto.getContent());
        assertEquals(false, resDto.isCompleted());
        verify(teamTodoMapper, times(2)).getTeamTodoById(eq(teamTodoId));
    }

    // 9. updateTeamTodo 테스트 - 실패 케이스 (데이터 없음)
    @Test
    void testUpdateTeamTodoFailNotFound() {
        // Given
        when(teamTodoMapper.getTeamTodoById(eq(teamTodoId))).thenReturn(null);

        // When
        ApiResponse response = teamTodoService.updateTeamTodo(teamTodoReqDto);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(UPDATE_TEAMTODO_FAIL, response.getMessage());
        assertNull(response.getData());
        verify(teamTodoMapper, times(1)).getTeamTodoById(eq(teamTodoId));
        verify(teamTodoMapper, never()).updateTeamTodo(any(TeamTodoDto.class));
    }

    // 10. updateTeamTodo 테스트 - 실패 케이스 (업데이트 에러)
    @Test
    void testUpdateTeamTodoFailUpdate() {
        // Given
        when(teamTodoMapper.getTeamTodoById(eq(teamTodoId))).thenReturn(teamTodoDto);
        doThrow(new RuntimeException("DB 에러")).when(teamTodoMapper).updateTeamTodo(any(TeamTodoDto.class));

        // When
        ApiResponse response = teamTodoService.updateTeamTodo(teamTodoReqDto);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(UPDATE_TEAMTODO_FAIL, response.getMessage());
        assertNull(response.getData());
        verify(teamTodoMapper, times(1)).getTeamTodoById(eq(teamTodoId));
        verify(teamTodoMapper, times(1)).updateTeamTodo(any(TeamTodoDto.class));
    }

    // 11. updateTeamTodo 테스트 - 성공 케이스
    @Test
    void testUpdateTeamTodoSuccess() {
        // Given
        when(teamTodoMapper.getTeamTodoById(eq(teamTodoId))).thenReturn(teamTodoDto);
        doNothing().when(teamTodoMapper).updateTeamTodo(any(TeamTodoDto.class));

        // When
        ApiResponse response = teamTodoService.updateTeamTodo(teamTodoReqDto);

        // Then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(UPDATE_TEAMTODO_SUCCESS, response.getMessage());
        assertNull(response.getData());
        verify(teamTodoMapper, times(1)).getTeamTodoById(eq(teamTodoId));
        verify(teamTodoMapper, times(1)).updateTeamTodo(any(TeamTodoDto.class));
    }

    // 12. deleteTeamTodo 테스트 - 실패 케이스 (데이터 없음)
    @Test
    void testDeleteTeamTodoFailNotFound() {
        // Given
        when(teamTodoMapper.getTeamTodoById(eq(teamTodoId))).thenReturn(null);

        // When
        ApiResponse response = teamTodoService.deleteTeamTodo(teamTodoId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(DELETE_TEAMTODO_FAIL, response.getMessage());
        assertNull(response.getData());
        verify(teamTodoMapper, times(1)).getTeamTodoById(eq(teamTodoId));
        verify(teamTodoMapper, never()).deleteTeamTodo(anyString());
    }

    // 13. deleteTeamTodo 테스트 - 실패 케이스 (삭제 에러)
    @Test
    void testDeleteTeamTodoFailDelete() {
        // Given
        when(teamTodoMapper.getTeamTodoById(eq(teamTodoId))).thenReturn(teamTodoDto);
        doThrow(new RuntimeException("DB 에러")).when(teamTodoMapper).deleteTeamTodo(anyString());

        // When
        ApiResponse response = teamTodoService.deleteTeamTodo(teamTodoId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(DELETE_TEAMTODO_FAIL, response.getMessage());
        assertNull(response.getData());
        verify(teamTodoMapper, times(1)).getTeamTodoById(eq(teamTodoId));
        verify(teamTodoMapper, times(1)).deleteTeamTodo(eq(teamTodoId));
    }

    // 14. deleteTeamTodo 테스트 - 성공 케이스
    @Test
    void testDeleteTeamTodoSuccess() {
        // Given
        when(teamTodoMapper.getTeamTodoById(eq(teamTodoId))).thenReturn(teamTodoDto);
        doNothing().when(teamTodoMapper).deleteTeamTodo(anyString());

        // When
        ApiResponse response = teamTodoService.deleteTeamTodo(teamTodoId);

        // Then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(DELETE_TEAMTODO_SUCCESS, response.getMessage());
        assertNull(response.getData());
        verify(teamTodoMapper, times(1)).getTeamTodoById(eq(teamTodoId));
        verify(teamTodoMapper, times(1)).deleteTeamTodo(eq(teamTodoId));
    }
}
