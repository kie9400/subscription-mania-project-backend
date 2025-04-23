package com.springboot.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ReviewDto {
    @Getter
    public static class Post{
        @Schema(description = "리뷰 내용", example = "본문이에용")
        @NotBlank(message = "리뷰 본문은 공백이어서는 안됩니다.")
        @Size(min = 1, max = 50, message = "본문은 1~50자 이내이어야 합니다." )
        private String content;

        @Schema(description = "별점", example = "3")
        @Range(min = 1, max = 5)
        private int rating;
    }

    @Getter
    public static class Patch{
        @Schema(hidden = true)
        @Setter
        private long reviewId;

        @Schema(description = "리뷰 내용", example = "본문이에용")
        @NotBlank(message = "리뷰 본문은 공백이어서는 안됩니다.")
        @Size(min = 1, max = 50, message = "본문은 1~50자 이내이어야 합니다." )
        private String content;

        @Schema(description = "별점", example = "3")
        @Range(min = 1, max = 5)
        private int rating;
    }
    @AllArgsConstructor
    @Getter
    @Builder
    @NoArgsConstructor
    public static class Response{
        @Schema(description = "리뷰 ID", example = "1")
        private long reviewId;
        @Schema(description = "리뷰 내용", example = "본문이에용")
        private String content;
        @Schema(description = "리뷰 작성일", example = "2025-03-21")
        private LocalDateTime createdAt;
        @Schema(description = "리뷰 추천 수", example = "22")
        private int recommendCount;
        @Schema(description = "별점", example = "4")
        private int rating;
        @Schema(description = "회원 ID", example = "1")
        private long memberId;
        @Schema(description = "작성자 명", example = "김철수")
        private String memberName;
        @Schema(description = "작성자 프로필 이미지", example = "/images/members/1/profile.png")
        private String memberImage;
    }
}
