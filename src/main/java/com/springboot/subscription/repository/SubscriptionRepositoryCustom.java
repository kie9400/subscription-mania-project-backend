package com.springboot.subscription.repository;

import com.springboot.subscription.entity.Subscription;

import java.util.List;

public interface SubscriptionRepositoryCustom {
    //사용자가 특정 플랫폼에 구독 등록상태인지 검증
    boolean existsSubsStatusByMemberAndPlatform(Long memberId, Long platformId);

    //수정할 구독Id를 제외한 특정 플랫폼에 구독 등록상태인지 검증
    boolean existsByMemberAndPlatformAndNotThisSubscription(Long memberId, Long platformId, Long subsId);

    //카테고리별 내 구독 내역 조회
    List<Subscription> findAllByMemberIdWithPlatformAndPlan(Long memberId);

    // 내가 구독한 플랫폼인지 검증
    boolean existsActiveSubscriptionByPlatform(Long memberId, Long platformId);
}
