package com.springboot.subscription.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SubscriptionDto {
    @Getter
    public static class Post{
        private long platformId;
        private long subsPlanId;
        private String billingCycle;
        private LocalDate subscriptionAt;
    }

    @Getter
    public static class Patch{
        private long subsPlanId;
        private String billingCycle;
        private LocalDateTime subscriptionAt;
    }

    @AllArgsConstructor
    @Getter
    @Builder
    @NoArgsConstructor
    public static class Response{
        private String platformName;
        private String platformImage;
        private String SubsPlanName;
        private LocalDateTime subscriptionStartAt;
        private LocalDateTime subscriptionEndAt;
        private LocalDateTime nextPaymentDate;
        private int price;
        private int totalPrice;
        private String billingCycle;
    }
}
