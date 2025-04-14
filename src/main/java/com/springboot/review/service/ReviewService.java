package com.springboot.review.service;

import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.platform.entity.Platform;
import com.springboot.platform.service.PlatformService;
import com.springboot.review.entity.Review;
import com.springboot.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemberService memberService;
    private final PlatformService platformService;

    public ReviewService(ReviewRepository reviewRepository, MemberService memberService, PlatformService platformService) {
        this.reviewRepository = reviewRepository;
        this.memberService = memberService;
        this.platformService = platformService;
    }

    public Review createReview(long platformId, Review review, long memberId){
        Member findMember = memberService.findVerifiedMember(memberId);
        Platform findPlatform = platformService.findVerifiedPlatform(platformId);
        review.setMember(findMember);
        review.setPlatform(findPlatform);
        Review savedReview = reviewRepository.save(review);

        //해당 플랫폼에 리뷰 추가 및 리뷰 개수, 별점 업데이트
        findPlatform.getReviews().add(review);
        Double avgRating = reviewRepository.getAverageRatingByPlatformId(platformId);
        Long reviewCount = reviewRepository.getReviewCountByPlatformId(platformId);

        findPlatform.setRatingAvg(avgRating);
        findPlatform.setReviewCount(reviewCount.intValue());

        return savedReview;
    }

}
