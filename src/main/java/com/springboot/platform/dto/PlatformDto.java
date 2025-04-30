package com.springboot.platform.dto;

import com.springboot.member.entity.Member;
import com.springboot.plan.dto.PlanDto;
import com.springboot.plan.entity.SubsPlan;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class PlatformDto {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Post{
        @Schema(description = "플랫폼 이름", example = "SPOTV NOW")
        private String platformName;

        @Schema(description = "플랫폼 설명", example = "스포츠 콘텐츠를 중심으로 제공하는 OTT 서비스")
        private String platformDescription;

        @Schema(description = "카테고리 ID", example = "1")
        private Long categoryId;

        @Schema(description = "서비스 시작일자", example = "2017-06-01")
        private LocalDate serviceAt;

        @Schema(description = "구독 플랜 목록",
                example = "[{\"planName\": \"베이직 이용권\", \"price\": 9900, \"billingCycle\": \"월\"}, " +
                        "{\"planName\": \"프리미엄 이용권\", \"price\": 19900, \"billingCycle\": \"월\"}]")
        private List<SubsPlan> subsPlans;
    }

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

    @Getter
    @AllArgsConstructor
    public static class PlatformStatisticsResponse {
        @Schema(description = "성별 통계 (예: MALE: 10, FEMALE: 15)")
        private Map<Member.Gender, Long> genderStats;

        @Schema(description = "연령대 통계 (예: 10대: 2, 20대: 15, 30대: 3)")
        private Map<Integer, Long> ageStats;
    }
}
