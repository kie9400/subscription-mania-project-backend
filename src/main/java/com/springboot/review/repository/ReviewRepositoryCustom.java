package com.springboot.review.repository;

public interface ReviewRepositoryCustom {
    //플랫폼에 평균 별점과 리뷰개수 업데이트하기 위해 조회
    Double getAverageRatingByPlatformId(Long platformId);
    Long getReviewCountByPlatformId(Long platformId);
    String findReviewStatusByMemberAndPlatform(long memberId, long platformId);
}

