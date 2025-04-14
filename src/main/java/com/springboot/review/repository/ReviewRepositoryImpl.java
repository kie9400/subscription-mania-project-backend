package com.springboot.review.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.review.entity.QReview;

public class ReviewRepositoryImpl implements ReviewRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public ReviewRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    QReview review = QReview.review;

    @Override
    public Double getAverageRatingByPlatformId(Long platformId) {
        return queryFactory
                .select(review.rating.avg())
                .from(review)
                .where(review.platform.platformId.eq(platformId))
                .fetchOne();
    }

    @Override
    public Long getReviewCountByPlatformId(Long platformId) {
        return queryFactory
                .select(review.count())
                .from(review)
                .where(review.platform.platformId.eq(platformId))
                .fetchOne();
    }

    @Override
    public String findReviewStatusByMemberAndPlatform(long memberId, long platformId) {
        return queryFactory
                .select(review.reviewStatus.stringValue())
                .from(review)
                .where(
                        review.member.memberId.eq(memberId),
                        review.platform.platformId.eq(platformId)
                )
                .fetchFirst();
    }
}
