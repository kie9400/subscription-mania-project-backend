package com.springboot.exception;

import lombok.Getter;

public enum ExceptionCode {
    MEMBER_NOT_FOUND(404,"회원을 찾을 수 없습니다."),
    MEMBER_EXISTS(409,"이미 가입된 회원입니다."),
    MEMBER_NOT_OWNER(403, "작성자만 가능합니다."),
    MEMBER_PHONE_NUMBER_EXISTS(409, "이 휴대폰 번호는 이미 존재합니다."),
    NOT_FOUND(404, "찾을 수 없습니다."),
    ALREADY_EXISTS(409,"이미 존재합니다.");

    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int statusCode, String message){
        this.message = message;
        this.status = statusCode;
    }
}
