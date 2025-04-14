package com.springboot.platform.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class PlatformRepositoryImpl {
    private final JPAQueryFactory queryFactory;

    public PlatformRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }
}
