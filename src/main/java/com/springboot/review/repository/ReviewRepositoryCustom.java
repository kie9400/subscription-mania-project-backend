package com.springboot.review.repository;

import com.springboot.member.entity.Member;
import com.springboot.platform.entity.Platform;
import com.springboot.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {
    //플랫폼에 평균 별점과 리뷰개수 업데이트하기 위해 조회
    Double getAverageRatingByPlatformId(Long platformId);
    Long getReviewCountByPlatformId(Long platformId);

    //리뷰 등록상태인지 확인하기위해 조회
    boolean existsReviewPostByMemberAndPlatform(Long memberId, Long platformId);

    //해당 플랫폼의 삭제된 리뷰를 제외한 모든 리뷰를 조회
    Page<Review> findActiveReviewsByPlatform(Platform platform, Pageable pageable);
}

