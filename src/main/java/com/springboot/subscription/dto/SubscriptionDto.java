package com.springboot.subscription.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

public class SubscriptionDto {
    @Getter
    public static class Post{
        @Schema(description = "플랫폼 ID", example = "1")
        private long platformId;

        @Schema(description = "구독 플랜 ID", example = "1")
        private long subsPlanId;

        @Schema(description = "구독 시작 날짜", example = "2025-04-15")
        private LocalDate subscriptionAt;
    }

    @Getter
    public static class Patch{
        @Schema(hidden = true)
        @Setter
        private long subscriptionId;

        @Schema(description = "플랫폼 ID", example = "1")
        private long platformId;

        @Schema(description = "구독 플랜 ID", example = "1")
        private long subsPlanId;

        @Schema(description = "구독 시작 날짜", example = "2025-04-15")
        private LocalDate subscriptionAt;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    public static class Response{
        @Schema(description = "플랫폼 ID", example = "1")
        private long platformId;

        @Schema(description = "플랫폼 이름", example = "넷플릭스")
        private String platformName;

        @Schema(description = "플랫폼 이미지", example = "/images/platform/netflix.png")
        private String platformImage;

        @Schema(description = "구독 플랜 명", example = "프리미엄")
        private String subsPlanName;

        @Schema(description = "구독 시작 날짜", example = "2025-04-15")
        private LocalDate subscriptionStartAt;

        @Schema(description = "다음 결제일", example = "2025-05-15")
        private LocalDate nextPaymentDate;

        @Schema(description = "요금", example = "12000")
        private int price;

        @Schema(description = "구독 주기", example = "월")
        private String billingCycle;
    }
}
