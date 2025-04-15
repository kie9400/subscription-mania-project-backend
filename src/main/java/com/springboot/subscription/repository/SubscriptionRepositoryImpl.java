package com.springboot.subscription.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class SubscriptionRepositoryImpl implements SubscriptionRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public SubscriptionRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }
}
