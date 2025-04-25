package com.springboot.review.repository;

import com.springboot.member.entity.Member;
import com.springboot.review.entity.Review;
import com.springboot.review.entity.ReviewRecommend;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRecommendRepository extends JpaRepository<ReviewRecommend, Long> {
    //존재 여부만 판단하는 existsBy가 성능이 약간 더 좋음
    boolean existsByReviewAndMember(Review review, Member member);
    Optional<ReviewRecommend> findByReviewAndMember(Review review, Member member);
    int countByReview(Review review);
}
