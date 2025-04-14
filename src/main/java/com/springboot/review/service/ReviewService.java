package com.springboot.review.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.platform.entity.Platform;
import com.springboot.platform.service.PlatformService;
import com.springboot.review.entity.Review;
import com.springboot.review.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    public Review createReview(Long platformId, Review review, Long memberId){
        Member findMember = memberService.findVerifiedMember(memberId);
        Platform findPlatform = platformService.findVerifiedPlatform(platformId);

        //리뷰 등록 중복 방지
        String existStatus = reviewRepository.findReviewStatusByMemberAndPlatform(memberId, platformId);
        if("REVIEW_POST".equals(existStatus)){
            throw new BusinessLogicException(ExceptionCode.ALREADY_EXISTS);
        }

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

    public Page<Review> findReviews(int page, int size, Long memberId, Long platformId){
        Member findMember = memberService.findVerifiedMember(memberId);
        Platform findPlatform = platformService.findVerifiedPlatform(platformId);
        Pageable pageable = PageRequest.of(page, size);

        return reviewRepository.findActiveReviewsByPlatform(findPlatform, pageable);
    }

    public void deleteReview(Long platformId, Long reviewId, Long memberId){
        Member findMember = memberService.findVerifiedMember(memberId);
        Platform findPlatform = platformService.findVerifiedPlatform(platformId);
        Review review = findVerifiedReview(reviewId);
        isReviewOwner(review, memberId);

        if(review.getReviewStatus().equals(Review.ReviewStatus.REVIEW_DELETE)){
            throw new BusinessLogicException(ExceptionCode.ALREADY_DELETED);
        }

        review.setReviewStatus(Review.ReviewStatus.REVIEW_DELETE);
        reviewRepository.save(review);
    }

    //리뷰 작성자인지 검증하는 메서드
    public void isReviewOwner(Review review, long memberId){
        memberService.isAuthenticatedMember(review.getMember().getMemberId(), memberId);
    }

    //리뷰가 존재하는지 검증하는 메서드
    public Review findVerifiedReview(long reviewId){
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        Review review = optionalReview.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.NOT_FOUND));

        return review;
    }
}
