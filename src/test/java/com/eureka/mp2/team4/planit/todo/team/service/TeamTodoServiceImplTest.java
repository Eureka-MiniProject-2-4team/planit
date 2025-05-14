package com.eureka.mp2.team4.planit.todo.team.service;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static com.eureka.mp2.team4.planit.todo.team.constants.TeamTodoMessages.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.team.dto.UserTeamDto;
import com.eureka.mp2.team4.planit.team.mapper.UserTeamMapper;
import com.eureka.mp2.team4.planit.todo.team.dto.TeamTodoDto;
import com.eureka.mp2.team4.planit.todo.team.dto.request.TeamTodoRequestDto;
import com.eureka.mp2.team4.planit.todo.team.dto.response.TeamTodoListResponseDto;
import com.eureka.mp2.team4.planit.todo.team.dto.response.TeamTodoResponseDto;
import com.eureka.mp2.team4.planit.todo.team.mapper.TeamTodoMapper;
import com.eureka.mp2.team4.planit.todo.team.mapper.TeamTodoUserMapper;
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
    @Mock
    private TeamTodoUserMapper teamTodoUserMapper;
    @Mock
    private UserTeamMapper userTeamMapper;

    @InjectMocks
    private TeamTodoServiceImpl teamTodoService;

    private String teamId;
    private String teamTodoId;
    private TeamTodoDto teamTodoDto;
    private TeamTodoRequestDto teamTodoRequestDto;
    private List<TeamTodoDto> teamTodoDtoList;
    private List<UserTeamDto> teamMembers = new ArrayList<>();

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
        teamTodoRequestDto = new TeamTodoRequestDto();
        teamTodoRequestDto.setId(teamTodoId);
        teamTodoRequestDto.setTeamId(teamId);
        teamTodoRequestDto.setTitle("테스트 할 일");
        teamTodoRequestDto.setContent("테스트 내용");
        teamTodoRequestDto.setCompleted(false);

        teamMembers = Arrays.asList(
                UserTeamDto.builder().userId("user1").teamId(teamId).build(),
                UserTeamDto.builder().userId("user2").teamId(teamId).build(),
                UserTeamDto.builder().userId("user3").teamId(teamId).build()
        );

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
    void testCreateTeamTodoFailOnCreateTodo() {
        // Given
        doThrow(new RuntimeException("DB 에러")).when(teamTodoMapper).createTeamTodo(any(TeamTodoDto.class));

        // When
        ApiResponse response = teamTodoService.createTeamTodo(teamTodoRequestDto);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(REGISTER_TEAMTODO_FAIL, response.getMessage());
        assertNull(response.getData());
        verify(teamTodoMapper, times(1)).createTeamTodo(any(TeamTodoDto.class));
        // teamTodoUserMapper는 호출되지 않아야 함
        verify(teamTodoUserMapper, never()).registerTeamTodoToMembers(anyString(), anyString());
        verify(userTeamMapper, never()).getTeamMemberList(anyString());
    }

    // 2. createTeamTodo 테스트 - 성공 케이스
    @Test
    void testCreateTeamTodoSuccess() {
        // Given
        doNothing().when(teamTodoMapper).createTeamTodo(any(TeamTodoDto.class));
        when(userTeamMapper.getTeamMemberList(eq(teamId))).thenReturn(teamMembers);
        doNothing().when(teamTodoUserMapper).registerTeamTodoToMembers(anyString(), anyString());

            // When
            ApiResponse response = teamTodoService.createTeamTodo(teamTodoRequestDto);

            // Then
            assertEquals(Result.SUCCESS, response.getResult());
            assertEquals(REGISTER_TEAMTODO_SUCCESS, response.getMessage());
            assertNull(response.getData());

            // TeamTodoMapper 호출 검증
            verify(teamTodoMapper, times(1)).createTeamTodo(any(TeamTodoDto.class));

            // 팀 멤버 목록 조회 검증
            verify(userTeamMapper, times(1)).getTeamMemberList(eq(teamId));

            // 각 팀 멤버에게 투두 등록 검증
            verify(teamTodoUserMapper, times(teamMembers.size())).registerTeamTodoToMembers(anyString(), anyString());

            // 각 팀 멤버별로 registerTeamTodoToMembers 호출 검증
            for (UserTeamDto member : teamMembers) {
                verify(teamTodoUserMapper, times(1)).registerTeamTodoToMembers(anyString(), eq(member.getUserId()));
            }

    }

    // 3. createTeamTodo 테스트 - 팀 멤버 조회 실패 케이스
    @Test
    void testCreateTeamTodoFailOnGetMembers() {
        // Given
        doNothing().when(teamTodoMapper).createTeamTodo(any(TeamTodoDto.class));
        when(userTeamMapper.getTeamMemberList(eq(teamId))).thenThrow(new RuntimeException("팀 멤버 조회 실패"));

        // When
        ApiResponse response = teamTodoService.createTeamTodo(teamTodoRequestDto);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(REGISTER_TEAMTODO_FAIL, response.getMessage());
        assertNull(response.getData());

        // TeamTodoMapper는 호출되었지만
        verify(teamTodoMapper, times(1)).createTeamTodo(any(TeamTodoDto.class));

        // 팀 멤버 목록 조회 검증
        verify(userTeamMapper, times(1)).getTeamMemberList(eq(teamId));

        // teamTodoUserMapper는 호출되지 않아야 함
        verify(teamTodoUserMapper, never()).registerTeamTodoToMembers(anyString(), anyString());
    }

    // 4. createTeamTodo 테스트 - 팀 멤버에게 투두 등록 실패 케이스
    @Test
    void testCreateTeamTodoFailOnRegisterToMembers() {
        // Given
        doNothing().when(teamTodoMapper).createTeamTodo(any(TeamTodoDto.class));
        when(userTeamMapper.getTeamMemberList(eq(teamId))).thenReturn(teamMembers);
        doThrow(new RuntimeException("멤버 투두 등록 실패")).when(teamTodoUserMapper).registerTeamTodoToMembers(anyString(), eq("user2"));

        // When
        ApiResponse response = teamTodoService.createTeamTodo(teamTodoRequestDto);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(REGISTER_TEAMTODO_FAIL, response.getMessage());
        assertNull(response.getData());

        // 검증
        verify(teamTodoMapper, times(1)).createTeamTodo(any(TeamTodoDto.class));
        verify(userTeamMapper, times(1)).getTeamMemberList(eq(teamId));
        // 첫 번째 멤버까지는 성공했을 수 있음
        verify(teamTodoUserMapper, atLeastOnce()).registerTeamTodoToMembers(anyString(), anyString());
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
        assertTrue(response.getData() instanceof TeamTodoListResponseDto);
        TeamTodoListResponseDto resDto = (TeamTodoListResponseDto) response.getData();
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
        assertTrue(response.getData() instanceof TeamTodoResponseDto);
        TeamTodoResponseDto resDto = (TeamTodoResponseDto) response.getData();
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
        ApiResponse response = teamTodoService.updateTeamTodo(teamTodoRequestDto);

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
        ApiResponse response = teamTodoService.updateTeamTodo(teamTodoRequestDto);

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
        ApiResponse response = teamTodoService.updateTeamTodo(teamTodoRequestDto);

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
