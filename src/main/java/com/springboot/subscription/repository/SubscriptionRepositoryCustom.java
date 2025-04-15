package com.springboot.subscription.repository;

public interface SubscriptionRepositoryCustom {
    //구독 등록상태인지 검증
    boolean existsSubsStatusByMemberAndPlatform(Long memberId, Long platformId);
}
