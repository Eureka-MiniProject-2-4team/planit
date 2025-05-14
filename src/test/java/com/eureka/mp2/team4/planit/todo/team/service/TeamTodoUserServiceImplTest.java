package com.eureka.mp2.team4.planit.todo.team.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.team.dto.UserTeamDto;
import com.eureka.mp2.team4.planit.team.mapper.UserTeamMapper;
import com.eureka.mp2.team4.planit.todo.team.dto.TeamTodoDto;
import com.eureka.mp2.team4.planit.todo.team.dto.TeamTodoUserDto;
import com.eureka.mp2.team4.planit.todo.team.dto.request.MyTeamTodoRequestDto;
import com.eureka.mp2.team4.planit.todo.team.dto.response.MyTeamListResponseDto;
import com.eureka.mp2.team4.planit.todo.team.dto.response.MyTeamTodoDetailResponseDto;
import com.eureka.mp2.team4.planit.todo.team.dto.response.TeamWithTodoResponseDto;
import com.eureka.mp2.team4.planit.todo.team.mapper.TeamTodoMapper;
import com.eureka.mp2.team4.planit.todo.team.mapper.TeamTodoUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.eureka.mp2.team4.planit.team.constants.TeamMessages.NOT_FOUND_MY_TEAMLIST;
import static com.eureka.mp2.team4.planit.todo.team.constants.TeamTodoMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamTodoUserServiceImplTest {

    @Mock
    private TeamTodoUserMapper myMapper;

    @Mock
    private TeamTodoMapper teamTodoMapper;

    @Mock
    private UserTeamMapper userTeamMapper;

    @InjectMocks
    private TeamTodoUserServiceImpl teamTodoUserService;

    private String userId;
    private String teamId;
    private String teamTodoId;
    private UserTeamDto userTeamDto;
    private TeamTodoDto teamTodoDto;
    private TeamTodoUserDto teamTodoUserDto;
    private List<UserTeamDto> userTeamDtoList;
    private List<TeamTodoDto> teamTodoDtoList;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        userId = "test-user-id";
        teamId = UUID.randomUUID().toString();
        teamTodoId = UUID.randomUUID().toString();

        // UserTeamDto 설정
        userTeamDto = UserTeamDto.builder()
                .userId(userId)
                .teamId(teamId)
                .joinedAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        // UserTeamDto 리스트 설정
        userTeamDtoList = Arrays.asList(userTeamDto);

        // TeamTodoDto 설정
        teamTodoDto = TeamTodoDto.builder()
                .id(teamTodoId)
                .teamId(teamId)
                .title("테스트 할 일")
                .content("테스트 내용")
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();

        // TeamTodoDto 리스트 설정
        teamTodoDtoList = Arrays.asList(
                teamTodoDto,
                TeamTodoDto.builder()
                        .id(UUID.randomUUID().toString())
                        .teamId(teamId)
                        .title("두 번째 할 일")
                        .content("두 번째 내용")
                        .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                        .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
                        .build()
        );

        // TeamTodoUserDto 설정
        teamTodoUserDto = TeamTodoUserDto.builder()
                .teamTodoId(teamTodoId)
                .userId(userId)
                .isCompleted(false)
                .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();
    }

    // 1. getMyTeamListAndTodoList 테스트 - 성공 케이스
    @Test
    void testGetMyTeamListAndTodoListSuccess() {
        // Given
        when(userTeamMapper.getMyTeamList(userId)).thenReturn(userTeamDtoList);
        when(teamTodoMapper.getTeamTodoList(teamId)).thenReturn(teamTodoDtoList);
        when(myMapper.getMyTeamTodoDetail(any(), eq(userId))).thenReturn(teamTodoUserDto);

        // When
        ApiResponse response = teamTodoUserService.getMyTeamListAndTodoList(userId);

        // Then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(GET_MY_TEAM_AND_TODO_LIST_SUCCESS, response.getMessage());
        assertNotNull(response.getData());

        MyTeamListResponseDto responseData = (MyTeamListResponseDto) response.getData();
        assertNotNull(responseData.getTeams());
        assertEquals(1, responseData.getTeams().size());

        TeamWithTodoResponseDto teamData = responseData.getTeams().get(0);
        assertEquals(teamId, teamData.getTeamId());
        assertNotNull(teamData.getTodos());
        assertEquals(2, teamData.getTodos().size());

        verify(userTeamMapper, times(1)).getMyTeamList(userId);
        verify(teamTodoMapper, times(1)).getTeamTodoList(teamId);
        verify(myMapper, times(2)).getMyTeamTodoDetail(any(), eq(userId));
    }

    // 2. getMyTeamListAndTodoList 테스트 - 팀 리스트가 없는 경우
    @Test
    void testGetMyTeamListAndTodoListWithNoTeams() {
        // Given
        when(userTeamMapper.getMyTeamList(userId)).thenReturn(null);

        // When
        ApiResponse response = teamTodoUserService.getMyTeamListAndTodoList(userId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(NOT_FOUND_MY_TEAMLIST, response.getMessage());
        assertNull(response.getData());

        verify(userTeamMapper, times(1)).getMyTeamList(userId);
        verify(teamTodoMapper, never()).getTeamTodoList(any());
        verify(myMapper, never()).getMyTeamTodoDetail(any(), any());
    }

    // 3. getMyTeamTodoDetail 테스트 - 성공 케이스
    @Test
    void testGetMyTeamTodoDetailSuccess() {
        // Given
        when(teamTodoMapper.getTeamTodoById(teamTodoId)).thenReturn(teamTodoDto);
        when(myMapper.getMyTeamTodoDetail(teamTodoId, userId)).thenReturn(teamTodoUserDto);

        // When
        ApiResponse response = teamTodoUserService.getMyTeamTodoDetail(teamId, teamTodoId, userId);

        // Then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(GET_MY_TEAM_TODO_SUCCESS, response.getMessage());
        assertNotNull(response.getData());

        MyTeamTodoDetailResponseDto responseData = (MyTeamTodoDetailResponseDto) response.getData();
        assertEquals(teamTodoDto.getTitle(), responseData.getTitle());
        assertEquals(teamTodoDto.getContent(), responseData.getContent());
        assertEquals(teamTodoUserDto.isCompleted(), responseData.isCompleted());
        assertEquals(teamTodoUserDto.getUpdatedAt(), responseData.getUpdatedAt());

        verify(teamTodoMapper, times(2)).getTeamTodoById(teamTodoId);
        verify(myMapper, times(1)).getMyTeamTodoDetail(teamTodoId, userId);
    }

    // 4. getMyTeamTodoDetail 테스트 - 팀 투두가 존재하지 않는 경우
    @Test
    void testGetMyTeamTodoDetailWithNoTeamTodo() {
        // Given
        when(teamTodoMapper.getTeamTodoById(teamTodoId)).thenReturn(null);

        // When
        ApiResponse response = teamTodoUserService.getMyTeamTodoDetail(teamId, teamTodoId, userId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(GET_MY_TEAM_TODO_FAIL, response.getMessage());
        assertNull(response.getData());

        verify(teamTodoMapper, times(1)).getTeamTodoById(teamTodoId);
        verify(myMapper, never()).getMyTeamTodoDetail(any(), any());
    }

    // 5. updateMyTeamTodo 테스트 - 성공 케이스
    @Test
    void testUpdateMyTeamTodoSuccess() {
        // Given
        MyTeamTodoRequestDto requestDto = new MyTeamTodoRequestDto();
        requestDto.setTeamTodoId(teamTodoId);
        requestDto.setUserId(userId);
        requestDto.setCompleted(false);

        when(teamTodoMapper.getTeamTodoById(teamTodoId)).thenReturn(teamTodoDto);
        doNothing().when(myMapper).updateMyTeamTodo(any(TeamTodoUserDto.class));

        // When
        ApiResponse response = teamTodoUserService.updateMyTeamTodo(requestDto);

        // Then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(UPDATE_MY_TEAMTODO_SUCCESS, response.getMessage());

        verify(teamTodoMapper, times(1)).getTeamTodoById(teamTodoId);
        verify(myMapper, times(1)).updateMyTeamTodo(any(TeamTodoUserDto.class));
    }

    // 6. updateMyTeamTodo 테스트 - 팀 투두가 존재하지 않는 경우
    @Test
    void testUpdateMyTeamTodoWithNoTeamTodo() {
        // Given
        MyTeamTodoRequestDto requestDto = new MyTeamTodoRequestDto();
        requestDto.setTeamTodoId(teamTodoId);
        requestDto.setUserId(userId);
        requestDto.setCompleted(true);

        when(teamTodoMapper.getTeamTodoById(teamTodoId)).thenReturn(null);

        // When
        ApiResponse response = teamTodoUserService.updateMyTeamTodo(requestDto);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(UPDATE_MY_TEAMTODO_FAIL, response.getMessage());

        verify(teamTodoMapper, times(1)).getTeamTodoById(teamTodoId);
        verify(myMapper, never()).updateMyTeamTodo(any(TeamTodoUserDto.class));
    }

    // 7. updateStatus 메서드 테스트
    @Test
    void testUpdateStatus() {
        // Given & When
        boolean result1 = teamTodoUserService.updateStatus(true);
        boolean result2 = teamTodoUserService.updateStatus(false);

        // Then
        assertFalse(result1);
        assertTrue(result2);
    }
}
