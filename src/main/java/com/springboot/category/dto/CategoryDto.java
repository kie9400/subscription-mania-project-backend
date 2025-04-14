package com.springboot.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class CategoryDto {
    @AllArgsConstructor
    @Getter
    public static class ResponseDto{
        @Schema(description = "카테고리 ID", example = "1")
        private long categoryId;
        @Schema(description = "카테고리 명", example = "문화")
        private String categoryName;
        @Schema(description = "카테고리 이미지", example = "category/culture.png")
        private String categoryImage;
    }
}
