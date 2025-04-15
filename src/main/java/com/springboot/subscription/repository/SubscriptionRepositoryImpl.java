package com.springboot.subscription.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.subscription.entity.QSubscription;
import com.springboot.subscription.entity.Subscription;

public class SubscriptionRepositoryImpl implements SubscriptionRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public SubscriptionRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    QSubscription subs = QSubscription.subscription;

    @Override
    public boolean existsSubsStatusByMemberAndPlatform(Long memberId, Long platformId) {
        Integer fetchOne = queryFactory
                //select 1과 같은것으로 주어진 테이블의 각 행에 대해 숫자1를 반환한다.
                .selectOne()
                .from(subs)
                .where(
                        subs.subsPlan.platform.platformId.eq(platformId),
                        subs.member.memberId.eq(memberId),
                        subs.subsStatus.eq(Subscription.SubsStatus.SUBSCRIBE_ACTIVE)
                )
                .fetchFirst();

        return fetchOne != null;
    }

    @Override
    public boolean existsByMemberAndPlatformAndNotThisSubscription(Long memberId, Long platformId, Long subsId) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(subs)
                .where(
                        subs.member.memberId.eq(memberId),
                        subs.subsPlan.platform.platformId.eq(platformId),
                        subs.subscriptionId.ne(subsId),
                        subs.subsStatus.ne(Subscription.SubsStatus.SUBSCRIBE_CANCEL)
                )
                .fetchFirst();
        return fetchOne != null;
    }
}
