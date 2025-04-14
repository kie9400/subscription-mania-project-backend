package com.springboot.review.repository;


import com.querydsl.jpa.impl.JPAQueryFactory;
import com.springboot.member.entity.Member;
import com.springboot.platform.entity.Platform;
import com.springboot.review.entity.QReview;
import com.springboot.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class ReviewRepositoryImpl implements ReviewRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public ReviewRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    //리뷰 테이블 Query 타입 생성
    QReview review = QReview.review;

    @Override
    public Double getAverageRatingByPlatformId(Long platformId) {
        return queryFactory
                .select(review.rating.avg())
                .from(review)
                .where(
                        review.platform.platformId.eq(platformId),
                        review.reviewStatus.eq(Review.ReviewStatus.REVIEW_POST) // 등록 상태만 포함
                )
                .fetchOne();
    }

    @Override
    public Long getReviewCountByPlatformId(Long platformId) {
        return queryFactory
                .select(review.count())
                .from(review)
                .where(
                        review.platform.platformId.eq(platformId),
                        review.reviewStatus.eq(Review.ReviewStatus.REVIEW_POST) // 등록 상태만 포함
                )
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

    @Override
    public Page<Review> findActiveReviewsByPlatform(Platform platform, Pageable pageable) {
        List<Review> content = queryFactory
                .selectFrom(review)
                .where(
                        // eq는 equal으로 =와 같다. ( review가 가지고있는 플랫폼과 인자로 받은 플랫폼이 같은지 )
                        review.platform.eq(platform),
                        // ne는 not equal으로 !=와 같다. ( 리뷰의 상태가 삭제 상태가 아닌것들만 )
                        review.reviewStatus.ne(Review.ReviewStatus.REVIEW_DELETE)
                )
                // getOffset의 값 번째 데이터부터 가져온다. ( getOffset은 page와 size를 곱한 값 )
                .offset(pageable.getOffset())
                // 한 페이지당 size 크기만큼 위에서부터 자른다. ( 한 페이지에 가져올 수 있는 페이지 크기 )
                .limit(pageable.getPageSize())
                // 리뷰 작성순으로 내림차순 정렬
                .orderBy(review.createdAt.desc())
                // 최종적으로 쿼리를 실행하여 결과 리스트를 가져옴 ( List 반환 )
                .fetch();

        // 리뷰의 총 개수
        Long total = queryFactory
                .select(review.count())
                .from(review)
                .where(
                        review.platform.eq(platform),
                        review.reviewStatus.ne(Review.ReviewStatus.REVIEW_DELETE)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }
}
