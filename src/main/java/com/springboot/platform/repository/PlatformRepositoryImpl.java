package com.springboot.platform.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.category.entity.QCategory;
import com.springboot.platform.entity.Platform;
import com.springboot.platform.entity.QPlatform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PlatformRepositoryImpl implements PlatformRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public PlatformRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    QPlatform platform = QPlatform.platform;
    QCategory category = QCategory.category;

    @Override
    public Page<Platform> findAllByCategory(Long categoryId, Pageable pageable) {
        // 조건 처리 ( category를 선택하지 않았을경우 null 처리 ) -> 전체조회, 선택했으면 카테고리별로 조회
        BooleanExpression condition = categoryId != null ? platform.category.categoryId.eq(categoryId) : null;

        // 콘텐츠 조회
        // 조회된 데이터를 DTO의 생성자 순서대로 넣어서 반환한다.
        List<Platform> platforms = queryFactory
                .selectFrom(platform)
                .leftJoin(platform.category, category).fetchJoin()
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //현재 페이지 데이터 + 전체 데이터 수
        Long total = queryFactory
                .select(platform.count())
                .from(platform)
                .leftJoin(platform.category, category)
                .where(condition)
                //단일 결과 하나만 가져오기
                .fetchOne();

        //total이 null이 아니면 그대로 쓰되 null이면 0
        return new PageImpl<>(platforms, pageable, total != null ? total : 0);
    }
}
