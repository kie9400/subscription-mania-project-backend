package com.springboot.member.dto;

import com.springboot.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

public class MemberDto {
    @Getter
    public static class EmailRequest{
        @NotBlank(message = "이메일은 공백이 아니어야 합니다.")
        @Email
        @Schema(description = "사용자 이메일", example = "example@gmail.com")
        private String email;
    }

    @Getter
    public static class VerifyCodeRequest {
        @NotBlank(message = "이메일은 공백이 아니어야 합니다.")
        @Email
        @Schema(description = "사용자 이메일", example = "example@gmail.com")
        private String email;

        @Pattern(regexp = "^\\d{6}$", message = "6자리 숫자여야 합니다.")
        @Schema(description = "6자리 인증 코드", example = "123456")
        private int code;
    }

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

    @Getter
    public static class FindId{
        @Schema(description = "사용자 이름", example = "홍성민")
        @NotBlank(message = "이름은 공백이 아니어야 합니다.")
        private String name;

        @Schema(description = "사용자 전화번호", example = "010-1111-2222")
        @Pattern(regexp = "^010-\\d{3,4}-\\d{4}$",
                message = "휴대폰 번호는 010으로 시작하는 11자리 숫자와 '-'로 구성되어야 합니다.")
        private String phoneNumber;
    }

    @Getter
    public static class Delete{
        @Schema(description = "사용자 이메일", example = "example@gmail.com")
        @NotBlank(message = "이메일은 공백이 아니어야 합니다.")
        @Email
        private String email;

        @NotBlank
        @Schema(description = "사용자 비밀번호", example = "password123!@")
        private String password;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @NoArgsConstructor
    @Builder
    public static class FindIdResponse{
        @Schema(description = "이메일", example = "email1@google.com")
        private String email;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @NoArgsConstructor
    @Builder
    public static class MyPageResponse{
        @Schema(description = "사용자 이름", example = "홍길동")
        private String name;

        @Schema(description = "사용자 프로필 이미지", example = "/images/members/1/profile.png")
        private String image;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @NoArgsConstructor
    @Builder
    public static class MyInfoResponse{
        @Schema(description = "사용자 이메일", example = "example@gmail.com")
        private String email;

        @Schema(description = "사용자 성별", example = "MALE")
        private Member.Gender gender;

        @Schema(description = "사용자 나이", example = "25")
        private int age;

        @Schema(description = "가입날짜", example = "2025-04-09")
        private LocalDateTime createdAt;

        @Schema(description = "휴대폰 번호", example = "010-1111-2222")
        private String phoneNumber;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @NoArgsConstructor
    @Builder
    public static class ReviewsResponse{
        private long reviewId;

        private String platformImage;

        private String platformName;

        private String content;

        private int rating;
    }
}
