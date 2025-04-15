package com.springboot.platform.dto;

import com.springboot.plan.dto.PlanDto;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class PlatformDto {
    @AllArgsConstructor
    @Getter
    @Builder
    @NoArgsConstructor
    public static class Response {
        @Parameter(description = "플랫폼 ID", example = "1")
        private long platformId;

        @Schema(description = "플랫폼 이름", example = "넷플릭스")
        private String platformName;

        @Schema(description = "플랫폼 이미지", example = "/images/platform/netflix.png")
        private String platformImage;

        @Schema(description = "플랫폼 설명", example = "다양한 영화와 드라마를 제공하는 OTT 서비스")
        private String platformDescription;

        @Schema(description = "카테고리 이름", example = "문화")
        private String categoryName;

        @Schema(description = "서비스 시작 날짜", example = "2016-01-07")
        private LocalDate serviceAt;

        @Schema(description = "평균 별점", example = "3.5")
        private double ratingAvg;

        @Schema(description = "리뷰 수", example = "0")
        private int reviewCount;

        @Schema(description = "구독 플랜 목록",
                example = "[{\"subsPlanId\": 1, \"planName\": \"광고형 스탠다드\", \"price\": 5500}, " +
                        "{\"subsPlanId\": 2, \"planName\": \"스탠다드\", \"price\": 13500}, " +
                        "{\"subsPlanId\": 3, \"planName\": \"프리미엄\", \"price\": 17000}]")
        private List<PlanDto.Response> plans;
    }

    @AllArgsConstructor
    @Getter
    @Builder
    @NoArgsConstructor
    public static class AllResponse{
        @Parameter(description = "플랫폼 ID", example = "1")
        private long platformId;

        @Schema(description = "플랫폼 이름", example = "넷플릭스")
        private String platformName;

        @Schema(description = "플랫폼 이미지", example = "/images/platform/netflix.png")
        private String platformImage;

        @Schema(description = "평균 별점", example = "3.5")
        private double ratingAvg;
    }
}
