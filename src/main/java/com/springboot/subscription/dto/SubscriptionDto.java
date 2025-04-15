package com.springboot.subscription.dto;

import java.time.LocalDateTime;

public class SubscriptionDto {
    public static class Post{
        long platformId;
        long subsPlanId;
        private String billingCycle;
        private LocalDateTime subscriptionAt;
    }
    public static class Patch{
        long subsPlanId;
        private String billingCycle;
        private LocalDateTime subscriptionAt;
    }
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
