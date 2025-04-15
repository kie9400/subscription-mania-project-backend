package com.springboot.subscription.repository;

public interface SubscriptionRepositoryCustom {
    //사용자가 특정 플랫폼에 구독 등록상태인지 검증
    boolean existsSubsStatusByMemberAndPlatform(Long memberId, Long platformId);

    //수정할 구독Id를 제외한 특정 플랫폼에 구독 등록상태인지 검증
    boolean existsByMemberAndPlatformAndNotThisSubscription(Long memberId, Long platformId, Long subsId);
}
