package com.springboot.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class ReviewDto {
    @Getter
    public static class Post{
        @Schema(description = "리뷰 내용", example = "본문이에용")
        @NotBlank(message = "리뷰 본문은 공백이어서는 안됩니다.")
        @Size(min = 1, max = 50, message = "본문은 1~50자 이내이어야 합니다." )
        private String content;

        @Schema(description = "별점", example = "3")
        @Size(min = 1, max = 5, message = "별점은 최소 1점은 주어야 합니다. (최대5)")
        private int rating;
    }

}
