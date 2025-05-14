package com.eureka.mp2.team4.planit.todo.team.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.team.dto.UserTeamDto;
import com.eureka.mp2.team4.planit.team.mapper.UserTeamMapper;
import com.eureka.mp2.team4.planit.todo.team.dto.TeamTodoDto;
import com.eureka.mp2.team4.planit.todo.team.dto.TeamTodoUserDto;
import com.eureka.mp2.team4.planit.todo.team.dto.request.MyTeamTodoRequestDto;
import com.eureka.mp2.team4.planit.todo.team.dto.response.*;
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
import java.util.Collections;
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
    // 1. getMyTeamListAndTodoList 테스트 - 성공 케이스
    @Test
    void testGetMyTeamListAndTodoListSuccess() {
        // Given
        // TeamTodoUser DTO 리스트 생성
        List<TeamTodoUserDto> teamTodoUserDtoList = Arrays.asList(
                TeamTodoUserDto.builder()
                        .title("테스트 할 일 1")
                        .content("테스트 내용 1")
                        .isCompleted(false)
                        .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                        .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
                        .build(),
                TeamTodoUserDto.builder()
                        .title("테스트 할 일 2")
                        .content("테스트 내용 2")
                        .isCompleted(true)
                        .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                        .updatedAt(Timestamp.valueOf(LocalDateTime.now()))
                        .build()
        );

        // 팀 투두 존재 여부 모킹
        when(teamTodoMapper.existTeamTodoByTeamId(eq(teamId))).thenReturn(1); // 팀 투두 존재

        // 내 팀 리스트 존재 여부 모킹
        when(userTeamMapper.getMyTeamList(eq(userId))).thenReturn(Arrays.asList(new UserTeamDto())); // 내 팀 리스트 존재

        // 내 팀 투두 리스트 조회 모킹
        when(myMapper.getMyTeamTodoList(eq(teamId), eq(userId))).thenReturn(teamTodoUserDtoList);

        // When
        ApiResponse response = teamTodoUserService.getMyTeamListAndTodoList(teamId, userId);

        // Then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(GET_MY_TEAM_AND_TODO_LIST_SUCCESS, response.getMessage());
        assertNotNull(response.getData());

        // 응답 데이터 검증
        MyTeamTodoListResponseDto responseData = (MyTeamTodoListResponseDto) response.getData();
        List<MyTeamTodoResponseDto> todoList = responseData.getMyTeamTodoStatus();

        assertEquals(2, todoList.size());
        assertEquals("테스트 할 일 1", todoList.get(0).getTitle());
        assertEquals("테스트 내용 1", todoList.get(0).getContent());
        assertFalse(todoList.get(0).isCompleted());

        assertEquals("테스트 할 일 2", todoList.get(1).getTitle());
        assertEquals("테스트 내용 2", todoList.get(1).getContent());
        assertTrue(todoList.get(1).isCompleted());

        // 메서드 호출 검증
        verify(teamTodoMapper, times(1)).existTeamTodoByTeamId(eq(teamId));
        verify(userTeamMapper, times(1)).getMyTeamList(eq(userId));
        verify(myMapper, times(1)).getMyTeamTodoList(eq(teamId), eq(userId));
    }

    // 2. getMyTeamListAndTodoList 테스트 - 팀 투두가 없는 경우
    @Test
    void testGetMyTeamListAndTodoListNoTeamTodo() {
        // Given
        // 팀 투두 없음 모킹
        when(teamTodoMapper.existTeamTodoByTeamId(eq(teamId))).thenReturn(null);

        // When
        ApiResponse response = teamTodoUserService.getMyTeamListAndTodoList(teamId, userId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(GET_MY_TEAM_AND_TODO_LIST_FAIL, response.getMessage()); // 일반적인 실패 메시지

        // 메서드 호출 검증
        verify(teamTodoMapper, times(1)).existTeamTodoByTeamId(eq(teamId));
        verify(userTeamMapper, never()).getMyTeamList(anyString()); // 팀 투두가 없으므로 이 메서드는 호출되지 않음
        verify(myMapper, never()).getMyTeamTodoList(anyString(), anyString()); // 팀 투두가 없으므로 이 메서드는 호출되지 않음
    }

    // 3. getMyTeamListAndTodoList 테스트 - 내 팀 리스트가 없는 경우
    @Test
    void testGetMyTeamListAndTodoListNoMyTeamList() {
        // Given
        // 팀 투두 존재하지만 내 팀 리스트 없음 모킹
        when(teamTodoMapper.existTeamTodoByTeamId(eq(teamId))).thenReturn(1); // 팀 투두 존재
        when(userTeamMapper.getMyTeamList(eq(userId))).thenReturn(null); // 내 팀 리스트 없음

        // When
        ApiResponse response = teamTodoUserService.getMyTeamListAndTodoList(teamId, userId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(GET_MY_TEAM_AND_TODO_LIST_FAIL, response.getMessage()); // 일반적인 실패 메시지

        // 메서드 호출 검증
        verify(teamTodoMapper, times(1)).existTeamTodoByTeamId(eq(teamId));
        verify(userTeamMapper, times(1)).getMyTeamList(eq(userId));
        verify(myMapper, never()).getMyTeamTodoList(anyString(), anyString()); // 내 팀 리스트가 없으므로 이 메서드는 호출되지 않음
    }

    // 4. getMyTeamListAndTodoList 테스트 - 팀 투두 목록 조회 시 예외 발생
    @Test
    void testGetMyTeamListAndTodoListExceptionOnFetchingTodoList() {
        // Given
        // 팀 투두 존재 및 내 팀 리스트 존재 모킹
        when(teamTodoMapper.existTeamTodoByTeamId(eq(teamId))).thenReturn(1); // 팀 투두 존재
        when(userTeamMapper.getMyTeamList(eq(userId))).thenReturn(Arrays.asList(new UserTeamDto())); // 내 팀 리스트 존재

        // 팀 투두 목록 조회 시 예외 발생 모킹
        when(myMapper.getMyTeamTodoList(eq(teamId), eq(userId)))
                .thenThrow(new RuntimeException("데이터베이스 오류"));

        // When
        ApiResponse response = teamTodoUserService.getMyTeamListAndTodoList(teamId, userId);

        // Then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(GET_MY_TEAM_AND_TODO_LIST_FAIL, response.getMessage());

        // 메서드 호출 검증
        verify(teamTodoMapper, times(1)).existTeamTodoByTeamId(eq(teamId));
        verify(userTeamMapper, times(1)).getMyTeamList(eq(userId));
        verify(myMapper, times(1)).getMyTeamTodoList(eq(teamId), eq(userId));
    }

    // 5. getMyTeamListAndTodoList 테스트 - 빈 투두 리스트 반환
    @Test
    void testGetMyTeamListAndTodoListEmptyTodoList() {
        // Given
        // 팀 투두 존재 및 내 팀 리스트 존재 모킹
        when(teamTodoMapper.existTeamTodoByTeamId(eq(teamId))).thenReturn(1); // 팀 투두 존재
        when(userTeamMapper.getMyTeamList(eq(userId))).thenReturn(Arrays.asList(new UserTeamDto())); // 내 팀 리스트 존재

        // 빈 투두 리스트 반환 모킹
        when(myMapper.getMyTeamTodoList(eq(teamId), eq(userId))).thenReturn(Collections.emptyList());

        // When
        ApiResponse response = teamTodoUserService.getMyTeamListAndTodoList(teamId, userId);

        // Then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(GET_MY_TEAM_AND_TODO_LIST_SUCCESS, response.getMessage());
        assertNotNull(response.getData());

        // 응답 데이터 검증 - 빈 리스트
        MyTeamTodoListResponseDto responseData = (MyTeamTodoListResponseDto) response.getData();
        assertTrue(responseData.getMyTeamTodoStatus().isEmpty());

        // 메서드 호출 검증
        verify(teamTodoMapper, times(1)).existTeamTodoByTeamId(eq(teamId));
        verify(userTeamMapper, times(1)).getMyTeamList(eq(userId));
        verify(myMapper, times(1)).getMyTeamTodoList(eq(teamId), eq(userId));
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
}
