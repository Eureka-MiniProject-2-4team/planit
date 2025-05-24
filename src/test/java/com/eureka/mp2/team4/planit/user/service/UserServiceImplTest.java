package com.eureka.mp2.team4.planit.user.service;

import com.eureka.mp2.team4.planit.common.ApiResponse;
import com.eureka.mp2.team4.planit.common.Result;
import com.eureka.mp2.team4.planit.common.exception.DatabaseException;
import com.eureka.mp2.team4.planit.common.exception.InternalServerErrorException;
import com.eureka.mp2.team4.planit.common.exception.NotFoundException;
import com.eureka.mp2.team4.planit.friend.FriendStatus;
import com.eureka.mp2.team4.planit.user.dto.UserDto;
import com.eureka.mp2.team4.planit.user.dto.request.UpdatePasswordRequestDto;
import com.eureka.mp2.team4.planit.user.dto.request.UpdateUserRequestDto;
import com.eureka.mp2.team4.planit.user.dto.response.MyPageResponseDto;
import com.eureka.mp2.team4.planit.user.dto.response.UserSearchForFriendResponseDto;
import com.eureka.mp2.team4.planit.user.dto.response.UserSearchForTeamResponseDto;
import com.eureka.mp2.team4.planit.user.dto.response.UserSearchResponseDto;
import com.eureka.mp2.team4.planit.user.enums.UserRole;
import com.eureka.mp2.team4.planit.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.eureka.mp2.team4.planit.user.constants.Messages.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private final String userId = "test-user-id";
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDto = new UserDto(
                "test-user-id",
                "test@planit.com",
                "테스트유저",
                "password",
                "닉네임",
                UserRole.ROLE_USER,
                null,
                null,
                true,
                "01012341234"
        );
    }

    @Test
    @DisplayName("성공 - 유저 정보 조회 (본인)")
    void getUserInfoSelfSuccess() {
        // given
        String nickname = "닉네임";

        when(userMapper.findActiveUserByNickName(nickname)).thenReturn(userDto);
        // when
        // when
        ApiResponse<?> response = userService.getUserInfo(userId, nickname, null);

        // then
        assertThat(response.getResult()).isEqualTo(Result.SUCCESS);
        assertThat(response.getData()).isInstanceOf(UserSearchResponseDto.class);

        UserSearchResponseDto dto = (UserSearchResponseDto) response.getData();
        assertThat(dto.getId()).isEqualTo(userId);
        assertThat(dto.getEmail()).isEqualTo("test@planit.com");
        assertThat(dto.getNickName()).isEqualTo(nickname);
        assertThat(dto.getIsMe()).isTrue();

        verify(userMapper).findActiveUserByNickName(nickname);
    }

    @Test
    @DisplayName("성공 - 마이페이지 조회")
    void getMyPageSuccess() {
        MyPageResponseDto myPageResponseDto = MyPageResponseDto.builder()
                .id("test-id")
                .email("test@email.com")
                .userName("test")
                .nickname("닉네임")
                .friendCount(1)
                .todoCount(2)
                .teamCount(3)
                .build();
        when(userMapper.findMyPageData("test-id")).thenReturn(myPageResponseDto);

        assertThat(userService.getMyPageData("test-id").getId()).isEqualTo("test-id");
    }

    @Test
    @DisplayName("실패 - 마이페이지 유저 정보 없음")
    void getMyPageDataUserNotFound() {
        when(userMapper.findUserById(userId)).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.getMyPageData(userId));

        assertThat(exception.getMessage()).isEqualTo(NOT_FOUND_USER);
    }

    @Test
    @DisplayName("실패 - 마이페이지 조회 시 내부 서버 에러 발생")
    void getMyPageDataInternalServerError() {
        when(userMapper.findMyPageData(userId)).thenThrow(InternalServerErrorException.class);

        assertThrows(InternalServerErrorException.class,
                () -> userService.getMyPageData(userId));
    }

    @Test
    @DisplayName("닉네임 수정 성공 테스트")
    void updateUser_success() {
        // given
        String userId = "user-123";
        UpdateUserRequestDto dto = new UpdateUserRequestDto("newNickname", null);

        // when
        ApiResponse response = userService.updateUser(userId, dto);

        // then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(UPDATE_NICKNAME_SUCCESS, response.getMessage());
        verify(userMapper, times(1)).updateNickName(userId, "newNickname");
    }

    @Test
    @DisplayName("닉네임 수정 실패 테스트 - DataAccessException 발생")
    void updateUser_dataAccessException() {
        // given
        String userId = "user-123";
        UpdateUserRequestDto dto = new UpdateUserRequestDto("newNickname", null);

        doThrow(new DataAccessException(UPDATE_NICKNAME_FAIL) {
        }).when(userMapper).updateNickName(userId, "newNickname");

        // when & then
        assertThrows(DatabaseException.class, () -> userService.updateUser(userId, dto));
        verify(userMapper, times(1)).updateNickName(userId, "newNickname");
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void updatePassword_success() {
        // given
        UpdatePasswordRequestDto dto = new UpdatePasswordRequestDto("newPassword");
        UserDto user = new UserDto(
                "test-user-id",
                "test@planit.com",
                "테스트유저",
                "encodedOldPass",
                "닉네임",
                UserRole.ROLE_USER,
                null,
                null,
                true,
                "01012341234"
        );

        when(userMapper.findUserById(userId)).thenReturn(user);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        // when
        ApiResponse<?> response = userService.updatePassword(userId, dto);

        // then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(UPDATE_PASSWORD_SUCCESS, response.getMessage());
        verify(userMapper).updatePassword(userId, "encodedPassword");
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 사용자 없음")
    void updatePassword_userNotFound() {
        // given
        UpdatePasswordRequestDto dto = new UpdatePasswordRequestDto("newPassword");
        when(userMapper.findUserById(userId)).thenReturn(null);

        // when & then
        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            userService.updatePassword(userId, dto);
        });

        assertEquals(NOT_FOUND_USER, ex.getMessage());
        verify(userMapper, never()).updatePassword(any(), any());
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - DB 예외 발생")
    void updatePassword_dbException() {
        // given
        UpdatePasswordRequestDto dto = new UpdatePasswordRequestDto("newPassword");
        UserDto user = new UserDto(
                "test-user-id",
                "test@planit.com",
                "테스트유저",
                "encodedOldPass",
                "닉네임",
                UserRole.ROLE_USER,
                null,
                null,
                true,
                "01012341234"
        );

        when(userMapper.findUserById(userId)).thenReturn(user);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");
        doThrow(new DataAccessException("DB 오류") {
        }).when(userMapper).updatePassword(userId, "encodedPassword");

        // when & then
        DatabaseException ex = assertThrows(DatabaseException.class, () -> {
            userService.updatePassword(userId, dto);
        });

        assertEquals(UPDATE_PASSWORD_FAIL, ex.getMessage());
    }

    @Test
    @DisplayName("isActive 업데이트 성공")
    void updateUser_isActive_success() {
        // given
        UpdateUserRequestDto dto = new UpdateUserRequestDto(null, false); // 닉네임 없음, 비활성화 요청

        // when
        ApiResponse<?> response = userService.updateUser(userId, dto);

        // then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(UPDATE_DISACTIVE, response.getMessage());

        verify(userMapper).updateIsActive(userId, false);
    }

    @Test
    @DisplayName("isActive 업데이트 실패 - DB 오류")
    void updateUser_isActive_fail_database() {
        // given
        UpdateUserRequestDto dto = new UpdateUserRequestDto(null, false);

        doThrow(new DataAccessException("DB 오류") {
        }).when(userMapper).updateIsActive(userId, false);

        // when & then
        DatabaseException ex = assertThrows(DatabaseException.class, () -> {
            userService.updateUser(userId, dto);
        });

        assertEquals(UPDATE_DISACTIVE_FAIL, ex.getMessage());
    }

    @Test
    @DisplayName("유저 삭제 성공")
    void deleteUser_success() {
        // given
        when(userMapper.isUserTeamLeader(userId)).thenReturn(false);

        // when
        ApiResponse<?> response = userService.deleteUser(userId);

        // then
        assertEquals(Result.SUCCESS, response.getResult());
        assertEquals(DELETE_USER_SUCCESS, response.getMessage());
        verify(userMapper).deleteUserById(userId);
    }

    @Test
    @DisplayName("유저가 팀장일 경우 삭제 실패")
    void deleteUser_fail_leaderExists() {
        // given
        when(userMapper.isUserTeamLeader(userId)).thenReturn(true);

        // when
        ApiResponse<?> response = userService.deleteUser(userId);

        // then
        assertEquals(Result.FAIL, response.getResult());
        assertEquals(LEADER_CAN_NOT_DELETE, response.getMessage());
        verify(userMapper, never()).deleteUserById(any());
    }

    @Test
    @DisplayName("DB 오류로 삭제 실패 - 예외 발생")
    void deleteUser_fail_databaseException() {
        // given
        when(userMapper.isUserTeamLeader(userId)).thenReturn(false);
        doThrow(new DataAccessException("DB 오류") {
        }).when(userMapper).deleteUserById(userId);

        // when & then
        DatabaseException ex = assertThrows(DatabaseException.class, () -> {
            userService.deleteUser(userId);
        });

        assertEquals(DELETE_USER_FAIL, ex.getMessage());
        verify(userMapper).deleteUserById(userId);
    }

    @Test
    @DisplayName("유저 정보 조회 - 친구 상태만 포함")
    void getUserInfo_friendOnly_success() {
        // given
        String currentUserId = "me";
        String value = "test@planit.com";
        String teamId = null;

        UserDto foundUser = new UserDto(
                "target-id", value, "테스트유저", "encoded", "닉네임",
                UserRole.ROLE_USER, null, null, true, "01012341234"
        );

        UserSearchForFriendResponseDto responseDto = new UserSearchForFriendResponseDto(
                "target-id",
                "test@planit.com",
                "닉네임",
                FriendStatus.ACCEPTED);

        when(userMapper.findActiveUserByEmail(value)).thenReturn(foundUser);
        when(userMapper.findUserWithFriendStatus(currentUserId, "target-id")).thenReturn(responseDto);

        // when
        ApiResponse<?> response = userService.getUserInfo(currentUserId, value, teamId);

        // then
        assertEquals(Result.SUCCESS, response.getResult());

        UserSearchForFriendResponseDto data = (UserSearchForFriendResponseDto) response.getData();
        assertEquals("target-id", data.getId());
        assertEquals(FriendStatus.ACCEPTED, data.getFriendStatus());
    }

    @Test
    @DisplayName("유저 정보 조회 - 팀 멤버십만 포함")
    void getUserInfo_teamOnly_success() {
        // given
        String currentUserId = "me";
        String value = "닉네임";
        String teamId = "team-123";

        UserDto foundUser = new UserDto(
                "target-id", "nick@a.com", "테스트유저", "encoded", value,
                UserRole.ROLE_USER, null, null, true, "01011112222"
        );

        UserSearchForTeamResponseDto responseDto = new UserSearchForTeamResponseDto(
                "target-id",
                "test@planit.com",
                "닉네임",
                "MEMBER");

        when(userMapper.findActiveUserByNickName(value)).thenReturn(foundUser);
        when(userMapper.findUserWithTeamStatus("target-id", teamId)).thenReturn(responseDto);

        // when
        ApiResponse<?> response = userService.getUserInfo(currentUserId, value, teamId);

        // then
        assertEquals(Result.SUCCESS, response.getResult());

        UserSearchForTeamResponseDto data = (UserSearchForTeamResponseDto) response.getData();
        assertEquals("target-id", data.getId());
        assertEquals("MEMBER", data.getTeamMembershipStatus());
    }

    @Test
    @DisplayName("유저 정보 조회 실패 - 존재하지 않는 유저")
    void getUserInfo_userNotFound() {
        // given
        String currentUserId = "me";
        String value = "없는유저";
        String teamId = null;

        when(userMapper.findActiveUserByNickName(value)).thenReturn(null);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> {
            userService.getUserInfo(currentUserId, value, teamId);
        });
        assertEquals(NOT_FOUND_USER, ex.getMessage());
    }

    @Test
    @DisplayName("유저 정보 조회 실패 - DB 오류 발생 시 예외 던짐")
    void getUserInfo_dataAccessException() {
        // given
        String currentUserId = "me";
        String value = "test@planit.com"; // 이메일 → findByEmail() 호출됨
        String teamId = null;

        when(userMapper.findActiveUserByEmail(value))
                .thenThrow(new DataAccessException("DB 오류") {
                });

        // when & then
        DatabaseException ex = assertThrows(DatabaseException.class, () -> {
            userService.getUserInfo(currentUserId, value, teamId);
        });

        assertEquals(FOUND_USER_FAIL, ex.getMessage()); // FOUND_USER_FAIL
    }

    @Test
    @DisplayName("이메일 입력 시 findByEmail()이 호출된다")
    void getUserInfo_callsFindByEmail() {
        // given
        String currentUserId = "me";
        String value = "test@planit.com"; // 이메일 형식
        String teamId = null;

        UserDto mockUser = new UserDto("id", value, "유저", "pw", "닉", UserRole.ROLE_USER, null, null, true, "010");
        when(userMapper.findActiveUserByEmail(value)).thenReturn(mockUser);

        // when
        userService.getUserInfo(currentUserId, value, teamId);

        // then
        verify(userMapper).findActiveUserByEmail(value);
        verify(userMapper, never()).findActiveUserByNickName(any());
    }

    @Test
    @DisplayName("닉네임 입력 시 findByNickName()이 호출된다")
    void getUserInfo_callsFindByNickName() {
        // given
        String currentUserId = "me";
        String value = "홍길동"; // 이메일 아님
        String teamId = null;

        UserDto mockUser = new UserDto("id", "email", "유저", "pw", value, UserRole.ROLE_USER, null, null, true, "010");
        when(userMapper.findActiveUserByNickName(value)).thenReturn(mockUser);

        // when
        userService.getUserInfo(currentUserId, value, teamId);

        // then
        verify(userMapper).findActiveUserByNickName(value);
        verify(userMapper, never()).findActiveUserByEmail(any());
    }

    @Test
    @DisplayName("자기 자신 검색 시 isMe=true로 응답한다")
    void getUserInfo_selfSearch_returnsIsMeTrue() {
        // given
        String currentUserId = "test-user-id";
        String value = "me@planit.com"; // 이메일로 검색
        String teamId = null;

        UserDto selfUser = new UserDto(
                currentUserId,
                value,
                "홍길동",
                "encoded-password",
                "닉네임",
                UserRole.ROLE_USER,
                null, null,
                true,
                "01012345678"
        );

        when(userMapper.findActiveUserByEmail(value)).thenReturn(selfUser);

        // when
        ApiResponse<?> response = userService.getUserInfo(currentUserId, value, teamId);

        // then
        assertEquals(Result.SUCCESS, response.getResult());

        UserSearchResponseDto data = (UserSearchResponseDto) response.getData();
        assertEquals(currentUserId, data.getId());
        assertTrue(data.getIsMe());

        verify(userMapper).findActiveUserByEmail(value);
    }

}