package com.springboot.exception;

import lombok.Getter;

public enum ExceptionCode {
    MEMBER_NOT_FOUND(404,"회원을 찾을 수 없습니다."),
    MEMBER_EXISTS(409,"이미 가입된 회원입니다."),
    MEMBER_NOT_OWNER(403, "작성자만 가능합니다."),
    MEMBER_NOT_ADMIN(403, "관리자 계정만 가능합니다."),
    MEMBER_DELETE(409, "이 회원은 탈퇴된 회원입니다."),
    PLATFORM_NOT_SUBSCRIPTION(403, "해당 플랫폼에 구독하지 않았습니다."),
    MEMBER_PHONE_NUMBER_EXISTS(409, "이 휴대폰 번호는 이미 존재합니다."),
    INVALID_REFRESH_TOKEN(400, "유효하지 않은 리플래시 토큰입니다."),
    INVALID_CODE(400, "인증코드가 일치하지 않습니다."),
    USER_NOT_LOGGED_IN(401, "로그인 하지 않은 유저입니다."),
    INVALID_SUBSCRIPTION_DATE(400, "구독 시작일은 플랫폼 서비스 시작일보다 빠를 수 없습니다."),
    ACCESS_DENIED(403, "FORBIDDEN, 접근 권한이 없습니다."),
    UNAUTHORIZED_ACCESS(403, "관리자 권한이 없습니다."),
    NOT_FOUND(404, "찾을 수 없습니다."),
    SEARCH_NOT_BLANK(400, "검색창이 비어 있습니다."),
    INVALID_CREDENTIALS(400,"비밀번호 또는 이메일이 틀렸습니다."),
    ALREADY_EXISTS(409,"이미 존재합니다."),
    ALREADY_DELETED(400,"이미 삭제되었습니다."),
    SEND_MAIL_FAILED(400,"메일 전송에 실패했습니다."),
    PASSWORD_NOT_MATCHED(400, "비밀번호가 일치하지 않습니다."),
    PASSWORD_SAME_AS_OLD(400, "새 비밀번호가 기존에 사용한 비밀번호와 동일합니다."),
    LOGOUT_ERROR(409, "로그아웃에 실패했습니다."),
    CATEGORY_NOT_FOUND(404,"카테고리를 찾을 수 없습니다."),
    IMAGE_NOT_FOUND(404,"이미지를 찾을 수 없습니다."),
    TOKEN_NOT_FOUND(401,"ACCESS_TOKEN_NOT_FOUND");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int statusCode, String message){
        this.message = message;
        this.status = statusCode;
    }
}
