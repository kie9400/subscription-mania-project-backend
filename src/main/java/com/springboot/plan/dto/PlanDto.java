package com.springboot.plan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PlanDto {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        @Schema(description = "구독 플랜 ID", example = "2")
        private Long subsPlanId;

        @Schema(description = "구독 플랜명", example = "스탠다드")
        private String planName;

        @Schema(description = "요금제", example = "13500")
        private int price;

        @Schema(description = "결제주기", example = "월")
        private String billingCycle;
    }
}
