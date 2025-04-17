package com.springboot.admin.dto;

import com.springboot.member.entity.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

public class AdminDto {
    @AllArgsConstructor
    @Getter
    public static class MemberResponse{
        @Schema(description = "사용자 ID", example = "1")
        private long memberId;

        @Schema(description = "사용자 이메일", example = "example@gmail.com")
        private String email;

        @Schema(description = "사용자 이름", example = "홍길동")
        private String name;

        @Schema(description = "사용자 프로필 이미지", example = "/images/members/1/profile.png")
        private String image;

        @Schema(description = "사용자 성별", example = "MALE")
        private Member.Gender gender;

        @Schema(description = "사용자 나이", example = "25")
        private int age;

        @Schema(description = "가입날짜", example = "2025-04-09")
        private LocalDateTime createdAt;

        @Schema(description = "휴대폰 번호", example = "010-1111-2222")
        private String phoneNumber;
    }
}
