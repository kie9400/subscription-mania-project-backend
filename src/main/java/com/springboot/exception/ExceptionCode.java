package com.springboot.exception;

import lombok.Getter;

public enum ExceptionCode {
    MEMBER_NOT_FOUND(404,"회원을 찾을 수 없습니다."),
    MEMBER_EXISTS(409,"이미 가입된 회원입니다."),
    MEMBER_NOT_OWNER(403, "작성자만 가능합니다."),
    MEMBER_PHONE_NUMBER_EXISTS(409, "이 휴대폰 번호는 이미 존재합니다."),
    INVALID_REFRESH_TOKEN(400, "유효하지 않은 리플래시 토큰입니다."),
    INVALID_CODE(400, "인증코드가 일치하지 않습니다."),
    USER_NOT_LOGGED_IN(401, "로그인 하지 않은 유저입니다."),
    ACCESS_DENIED(403, "접근 권한이 없습니다."),
    UNAUTHORIZED_ACCESS(403, "관리자 권한이 없습니다."),
    NOT_FOUND(404, "찾을 수 없습니다."),
    SEARCH_NOT_BLANK(400, "검색창이 비어 있습니다."),
    ALREADY_EXISTS(409,"이미 존재합니다."),
    SEND_MAIL_FAILED(400,"메일 전송에 실패했습니다."),
    LOGOUT_ERROR(409, "로그아웃에 실패했습니다.");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int statusCode, String message){
        this.message = message;
        this.status = statusCode;
    }
}
