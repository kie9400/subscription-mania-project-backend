package com.springboot.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class LoginDto {
    @Schema(description = "사용자 이메일", example = "example@gmail.com")
    private String username;

    @Schema(description = "사용자 비밀번호", example = "password123!@")
    private String password;
}
