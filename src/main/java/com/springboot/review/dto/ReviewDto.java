package com.springboot.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
        @Schema(description = "별점", example = "4")
        private int rating;
        @Schema(description = "회원 ID", example = "1")
        private long memberId;
        @Schema(description = "작성자명", example = "김철수")
        private String memberName;
    }
}
