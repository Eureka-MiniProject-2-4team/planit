package com.eureka.mp2.team4.planit.friend.constants;

import javax.annotation.processing.Generated;

@Generated("Excluded from coverage")
public class FriendMessages {

    // 친구 요청
    public static final String REQUEST_SUCCESS = "친구 요청 전송 완료";
    public static final String REQUEST_FAIL = "친구 요청 전송 실패";
    public static final String REQUEST_ALREADY_EXISTS = "이미 친구 요청이 존재합니다.";
    public static final String RECEIVER_NOT_FOUND = "해당 유저가 존재하지 않습니다.";

    // 친구 목록 조회
    public static final String GET_FRIENDS_SUCCESS = "친구 목록 조회 성공";
    public static final String GET_FRIENDS_FAIL = "친구 목록 조회 실패";

    // 받은 요청 조회
    public static final String GET_PENDING_SUCCESS = "받은 요청 목록 조회 성공";
    public static final String GET_PENDING_FAIL = "받은 요청 목록 조회 실패";

    // 보낸 요청 조회
    public static final String GET_SENT_SUCCESS = "보낸 요청 목록 조회 성공";
    public static final String GET_SENT_FAIL = "보낸 요청 목록 조회 실패";

    // 요청 상태 업데이트
    public static final String UPDATE_STATUS_SUCCESS = "요청 상태 업데이트 완료";
    public static final String UPDATE_STATUS_FAIL = "요청 상태 업데이트 실패";

    // 친구 삭제
    public static final String DELETE_SUCCESS = "친구 삭제 완료";
    public static final String DELETE_FAIL = "친구 삭제 실패";

    // 친구 검색
    public static final String SEARCH_SUCCESS = "친구 검색 성공";
    public static final String SEARCH_FAIL = "친구 검색 실패";

    // 기타
    public static final String NOT_FOUND_ID = "해당 친구 ID가 존재하지 않습니다!";

    public static final String ALREADY_FRIEND = "이미 친구입니다.";
    public static final String ALREADY_SENT_REQUEST = "이미 친구 요청을 보냈습니다.";
    public static final String REQUEST_EXISTS_FROM_RECEIVER = "상대가 이미 친구 요청을 보냈습니다.";

}
