package com.example.loginservice.global;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {
    /**
     * 2000 : 요청 성공
     */
    SUCCESS(true, 2000, "요청에 성공하였습니다."),


    /**
     * 4000 : Request 오류
     */
    // Common
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),
    RTK_INCORRECT(false, 2004, "Refresh Token 값을 확인해주세요."),
    USERS_NOT_FOUND_EMAIL(true,2010,"가입 가능한 이메일입니다."),
    USERS_EXISTS_EMAIL(false,2011,"이미 존재하는 메일 주소입니다."),

    USERS_NOT_EXISTS(false,2012,"회원 정보가 존재하지 않습니다."),
    UNABLE_TO_SEND_EMAIL(false, 2020, "이메일을 발송하지 못했습니다."),
    ERR_MAKE_CODE(false, 2021, "인증 코드 생성에 오류가 있습니다."),
    INCORRECT_CODE(false, 2022, "인증 코드가 일치하지 않습니다."),
    INCORRECT_EMAIL(false, 2023, "이메일이 일치하지 않습니다."),

    NOT_EXISTS(false, 4000, "삭제되었거나 존재하지 않습니다."),
    BAD_ACCESS(false,4001,"잘못된 접근입니다."),
    TASK_FAILED(false, 4002, "작업에 실패했습니다. 다시 시도해주세요."),
    LOGIN_EXPIRED(false,4003, "인증이 만료되었습니다. 다시 로그인해주세요."),
    PASSWORD_NOT_MATCH(false, 4004,"비밀번호가 틀렸습니다." ),
    EXPIRED_VERIFY(false,4005,"이 인증은 이미 만료되었습니다."),
    EXPIRED_ACCESS_TOKEN(false, 4006, "만료된 액세스 토큰 입니다."),
    LIKE_ALREADY_EXISTS(false, 4010, "이미 좋아한 게시글입니다."),
    LIKE_NOT_EXISTS(false, 4011, "좋아요를 누르지 않아서 취소할 수 없습니다."),
    CANNOT_LIKE_MINE(false,4012, "내 게시물은 좋아할 수 없습니다."),

    CANNOT_CLIP_MINE(false,4013 , "내 게시물은 스크랩하지 않습니다."),
    WAITING_ALREADY_EXISTS(false, 4014, "이 모임에 대기하고 있습니다. 모임장이 승락할 때까지 기다려주세요."),

    TEAM_ALREADY_JOINED(false,4015, "이미 이 모임원입니다!" ),
    CURRENT_UNAVAILABLE(false,4016 ,"지금은 가능하지 않습니다." ),
    USER_NOT_EXISTS(false, 4017, "해당 회원이 존재하지 않습니다."),

    ALREADY_RECRUIT(false, 4018, "이미 모집중이거나, 모집을 중단했습니다."),
    NEED_MORE_WRITE(false,4019 ,"작성을 더 확인해주세요." ),

    LOGIN_FIRST(false,4020,"먼저 로그인을 진행해주세요."),
    NOT_EDIT(false,4021,"변경할 필요가 없습니다."),
    NICKNAME_CHANGE_AVAILABLE(false, 4022, "닉네임 변경이 너무 잦습니다. 이전 변경으로 부터 3개월 이후에 시도해주세요."),
    DUPLICATE_NICKNAME(false, 4023, "중복된 정보가 있습니다. 다른 것으로 설정해주세요."),
    NUM_FILE_OVER(false, 4024, "허가된 파일 개수를 넘었습니다."),
    FILE_FORMAT_ERROR(false, 4025,"사용 가능한 파일 형태가 아닙니다. (png,jpg,jpeg가능)" ),
    ALREADY_SCORED(false, 4026, "이미 점수를 부여했습니다." );




    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
