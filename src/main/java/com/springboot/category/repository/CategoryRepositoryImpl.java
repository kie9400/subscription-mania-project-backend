package com.springboot.category.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

public class CategoryRepositoryImpl {
    private final JPAQueryFactory queryFactory;

    public CategoryRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }
}
