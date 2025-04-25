package com.springboot.subscription.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.category.entity.QCategory;
import com.springboot.plan.entity.QSubsPlan;
import com.springboot.platform.entity.QPlatform;
import com.springboot.subscription.entity.QSubscription;
import com.springboot.subscription.entity.Subscription;

import java.util.List;

public class SubscriptionRepositoryImpl implements SubscriptionRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public SubscriptionRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    QSubscription subs = QSubscription.subscription;
    QSubsPlan plan = QSubsPlan.subsPlan;
    QPlatform platform = QPlatform.platform;

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

    @Override
    public List<Subscription> findAllByMemberIdWithPlatformAndPlan(Long memberId) {
        QCategory category = QCategory.category;

        return queryFactory.selectFrom(subs)
                .join(subs.subsPlan, plan).fetchJoin()
                .join(plan.platform, platform).fetchJoin()
                .join(platform.category, category).fetchJoin()
                .where(
                        subs.member.memberId.eq(memberId),
                        subs.subsStatus.eq(Subscription.SubsStatus.SUBSCRIBE_ACTIVE)
                )
                .fetch();
    }

    @Override
    public boolean existsActiveSubscriptionByPlatform(Long memberId, Long platformId) {
        Integer result = queryFactory
                .selectOne()
                .from(subs)
                .join(subs.subsPlan, plan)
                .join(plan.platform, platform)
                .where(
                        subs.member.memberId.eq(memberId),
                        platform.platformId.eq(platformId),
                        subs.subsStatus.eq(Subscription.SubsStatus.SUBSCRIBE_ACTIVE)
                )
                .fetchFirst();

        return result != null;
    }
}
