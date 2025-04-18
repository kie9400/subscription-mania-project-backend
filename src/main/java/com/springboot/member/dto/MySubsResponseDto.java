package com.springboot.member.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MySubsResponseDto {
    // 이번 달 총 요금
    private int totalMonthlyPrice;

    private List<CategoryGroup> categories;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CategoryGroup {
        private String categoryName;
        private String categoryImage;
        private int categoryTotalPrice;

        private List<SubscriptionInfo> subscriptions;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SubscriptionInfo {
        private long subscriptionId;
        private String platformImage;
        private String platformName;
        private String planName;
        private String billingCycle;
        private int price;
    }
}
