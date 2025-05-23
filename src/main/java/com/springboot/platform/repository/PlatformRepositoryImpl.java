package com.springboot.platform.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.category.entity.QCategory;
import com.springboot.member.entity.Member;
import com.springboot.member.entity.QMember;
import com.springboot.plan.entity.QSubsPlan;
import com.springboot.platform.entity.Platform;
import com.springboot.platform.entity.QPlatform;
import com.springboot.subscription.entity.QSubscription;
import com.springboot.subscription.entity.Subscription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlatformRepositoryImpl implements PlatformRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public PlatformRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    QPlatform platform = QPlatform.platform;
    QCategory category = QCategory.category;
    QSubscription subscription = QSubscription.subscription;
    QSubsPlan subsPlan = QSubsPlan.subsPlan;
    QMember member = QMember.member;

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

    @Override
    public List<Platform> findTopRatedPlatforms(int limit) {
        return queryFactory
                .selectFrom(platform)
                .orderBy(platform.ratingAvg.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<Platform> findPopularPlatformsByAge(int age, int limit) {
        int ageGroup = (age / 10) * 10;

        return queryFactory
                .select(subscription.subsPlan.platform)
                .from(subscription)
                .join(subscription.member, member)
                .where(
                        //WHERE member.age BETWEEN 20 AND 29와 같음
                        member.age.between(ageGroup, ageGroup + 9)
                )
                .groupBy(subscription.subsPlan.platform)
                .orderBy(subscription.subsPlan.platform.count().desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public Map<Integer, Long> countByPlatformSubsByAgeGroup(Long platformId) {
        return queryFactory
                .select(
                        Expressions.numberTemplate(Integer.class, "FLOOR({0} / 10) * 10", member.age),
                        subscription.count()
                )
                .from(subscription)
                .join(subscription.member, member)
                .where(
                        subscription.subsPlan.platform.platformId.eq(platformId),
                        member.memberStatus.eq(Member.MemberStatus.MEMBER_ACTIVE),
                        subscription.subsStatus.eq(Subscription.SubsStatus.SUBSCRIBE_ACTIVE)
                )
                .groupBy(Expressions.numberTemplate(Integer.class, "FLOOR({0} / 10) * 10", member.age))
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(0, Integer.class),
                        tuple -> tuple.get(1, Long.class)
                ));
    }

    @Override
    public Map<Member.Gender, Long> countByPlatformSubsByGender(Long platformId) {
        return queryFactory
                .select(member.gender, subscription.count())
                .from(subscription)
                .join(subscription.member, member)
                .where(
                        subscription.subsPlan.platform.platformId.eq(platformId),
                        member.memberStatus.eq(Member.MemberStatus.MEMBER_ACTIVE),
                        subscription.subsStatus.eq(Subscription.SubsStatus.SUBSCRIBE_ACTIVE)
                )
                .groupBy(member.gender)
                .fetch()
                .stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(member.gender),
                        tuple -> tuple.get(subscription.count())
                ));
    }

    @Override
    public Page<Platform> findByPlatforms(Pageable pageable) {
        List<Platform> platforms = queryFactory
                .selectFrom(platform)
                .leftJoin(platform.category, category)  // 카테고리와 조인
                .where(platform.platformId.isNotNull())  // 기본 조건 (platformId가 null이 아닌 것)
                .offset(pageable.getOffset())  // 페이징 시작 위치
                .limit(pageable.getPageSize())  // 한 페이지 당 크기
                .orderBy(platform.platformId.asc())  // 예시: 플랫폼 ID 기준 오름차순 정렬
                .fetch();

        //현재 페이지 데이터 + 전체 데이터 수
        Long total = queryFactory
                .select(platform.count())
                .from(platform)
                .fetchOne();

        return new PageImpl<>(platforms, pageable, total != null ? total : 0);
    }
}
