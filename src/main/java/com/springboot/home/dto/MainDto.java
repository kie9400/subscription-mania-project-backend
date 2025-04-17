package com.springboot.home.dto;

import com.springboot.category.dto.CategoryDto;
import com.springboot.platform.dto.PlatformDto;
import com.springboot.subscription.dto.SubscriptionDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MainDto {
    @Schema(description = "카테고리 리스트", example = "[{\"categoryId\":1,\"categoryName\":\"문화\",\"categoryImage\":\"/images/category/culture.png\"}]")
    List<CategoryDto.Response> categories;

    @Schema(description = "추천 플랫폼 리스트", example = "[{\"platformId\":1,\"platformName\":\"넷플릭스\",\"platformImage\":\"/images/platform/netflix.png\",\"ratingAvg\":4.5}]")
    List<PlatformDto.AllResponse> platforms;
}
