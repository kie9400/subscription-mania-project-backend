package com.springboot.platform.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
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
    public Page<Platform> findByCategoryAndKeywordAndRating(Long categoryId, String keyword, Integer rating, Pageable pageable, String sort) {
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sort);

        BooleanBuilder builder = new BooleanBuilder();
        if (categoryId != null) {
            builder.and(platform.category.categoryId.eq(categoryId));
        }

        if (keyword != null && !keyword.isBlank()) {
            String processedKeyword = keyword.replace(" ", "");

            //Expressions는 QueryDSL에서 SQL 함수를 직접 표현하고 싶을 때 사용한다.
            //stringTemplate()메서드는 문자열 기반 SQL 함수 작성할때 사용
            //밑에는 replace함수를 사용하여 데이터에 공백을 제거하고 비교한다.
            builder.and(
                    Expressions.stringTemplate("replace({0}, ' ', '')", platform.platformName)
                            .containsIgnoreCase(processedKeyword)
            );
        }

        if (rating != null) {
            builder.and(platform.ratingAvg.goe(rating));
            builder.and(platform.ratingAvg.lt(rating + 1));
        }

        // 콘텐츠 조회
        // 조회된 데이터를 DTO의 생성자 순서대로 넣어서 반환한다.
        List<Platform> platforms = queryFactory
                .selectFrom(platform)
                .leftJoin(platform.category, category).fetchJoin()
                .where(builder)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        //현재 페이지 데이터 + 전체 데이터 수
        Long total = queryFactory
                .select(platform.count())
                .from(platform)
                .leftJoin(platform.category, category)
                .where(builder)
                //단일 결과 하나만 가져오기
                .fetchOne();

        //total이 null이 아니면 그대로 쓰되 null이면 0
        return new PageImpl<>(platforms, pageable, total != null ? total : 0);
    }

    @Override
    public Page<Platform> findByCategoryAndRating(Long categoryId, Integer rating, Pageable pageable, String sort) {
        BooleanBuilder builder = new BooleanBuilder();

        if (categoryId != null) {
            builder.and(platform.category.categoryId.eq(categoryId));
        }

        if (rating != null) {
            builder.and(platform.ratingAvg.goe(rating)); // 평균 별점 이상
            builder.and(platform.ratingAvg.lt(rating + 1)); // 예: 3 이상 4 미만
        }

        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(sort); // 정렬 기준

        List<Platform> results = queryFactory
                .selectFrom(platform)
                .leftJoin(platform.category, category).fetchJoin()
                .where(builder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderSpecifier)
                .fetch();

        Long total = queryFactory
                .select(platform.count())
                .from(platform)
                .leftJoin(platform.category, category)
                .where(builder)
                .fetchOne();

        return new PageImpl<>(results, pageable, total != null ? total : 0);
    }

    //정렬 기준
    private OrderSpecifier<?> getOrderSpecifier(String sort) {
        switch (sort) {
            case "rating":
                return platform.ratingAvg.desc();
            case "review":
                return platform.reviewCount.desc();
            default:
                return platform.platformId.asc(); // 기본 정렬: 등록순
        }
    }
}
