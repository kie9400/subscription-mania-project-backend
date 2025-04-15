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

        //리뷰 개수, 별점 업데이트
        //해당 플랫폼에 리뷰 추가 및 리뷰 개수, 별점 업데이트
        findPlatform.getReviews().add(review);
        updatePlatformReviewStats(findPlatform, savedReview);

        return savedReview;
    }

    public Review updateReview(Long platformId, Review review, Long memberId){
        Member findMember = memberService.findVerifiedMember(memberId);
        Platform findPlatform = platformService.findVerifiedPlatform(platformId);
        Review findreview = findVerifiedReview(review.getReviewId());

        //삭제된 리뷰인지 검증
        if(findreview.getReviewStatus().equals(Review.ReviewStatus.REVIEW_DELETE)){
            throw new BusinessLogicException(ExceptionCode.ALREADY_DELETED);
        }

        //작성자 인지 검증
        isReviewOwner(findreview, memberId);

        Optional.ofNullable(review.getContent())
                .ifPresent(content -> findreview.setContent(content));
        Optional.of(review.getRating())
                .ifPresent(rating -> findreview.setRating(rating));

        Review saveReview = reviewRepository.save(findreview);
        updatePlatformAverageRatingOnly(findPlatform);
        return saveReview;
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

        //리뷰 개수, 별점 업데이트
        updatePlatformReviewStats(findPlatform, review);
    }

    //해당 플랫폼에 평균 별점, 리뷰 수 갱신 메서드
    public void updatePlatformReviewStats(Platform platform, Review review){
        Double avgRating = reviewRepository.getAverageRatingByPlatformId(platform.getPlatformId());
        Long reviewCount = reviewRepository.getReviewCountByPlatformId(platform.getPlatformId());

        platform.setRatingAvg(avgRating != null ? avgRating : 0.0);
        platform.setReviewCount(reviewCount != null ? reviewCount.intValue() : 0);
    }

    //평균 별점만 갱신하는 메서드
    private void updatePlatformAverageRatingOnly(Platform platform) {
        Double avgRating = reviewRepository.getAverageRatingByPlatformId(platform.getPlatformId());
        platform.setRatingAvg(avgRating != null ? avgRating : 0.0);
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
