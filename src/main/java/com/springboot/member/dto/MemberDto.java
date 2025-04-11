package com.springboot.member.dto;

import com.springboot.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public class MemberDto {
    @Getter
    public static class Post {
        @NotBlank(message = "이메일은 공백이 아니어야 합니다.")
        @Email
        @Schema(description = "사용자 이메일", example = "example@gmail.com")
        private String email;

        @NotBlank
        @Schema(description = "사용자 비밀번호", example = "password123!@")
        private String password;

        @Schema(description = "사용자 성별", example = "MALE")
        private Member.Gender gender;

        @Schema(description = "사용자 나이", example = "25")
        private int age;

        @Schema(description = "사용자 이름", example = "홍길동")
        private String name;

        @Schema(description = "휴대폰 번호", example = "010-1111-2222")
        private String phoneNumber;
    }
}
