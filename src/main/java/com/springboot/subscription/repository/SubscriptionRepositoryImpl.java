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
}
