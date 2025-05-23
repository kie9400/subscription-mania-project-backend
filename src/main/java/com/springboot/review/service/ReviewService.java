package com.springboot.review.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.platform.entity.Platform;
import com.springboot.platform.service.PlatformService;
import com.springboot.review.entity.Review;
import com.springboot.review.entity.ReviewRecommend;
import com.springboot.review.repository.ReviewRecommendRepository;
import com.springboot.review.repository.ReviewRepository;
import com.springboot.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final MemberService memberService;
    private final ReviewRecommendRepository reviewRecommendRepository;
    private final PlatformService platformService;
    private final SubscriptionService subscriptionService;

    public Review createReview(Long platformId, Review review, Long memberId){
        Member findMember = memberService.findVerifiedMember(memberId);
        Platform findPlatform = platformService.findVerifiedPlatform(platformId);

        //회원이 해당 플랫폼 서비스에 구독중인지 검증(구독 내역이 없다면 예외발생)
        if(!subscriptionService.findSubsWithMemberAndPlatform(findMember.getMemberId(), findPlatform.getPlatformId())){
            throw new BusinessLogicException(ExceptionCode.PLATFORM_NOT_SUBSCRIPTION);
        }

        //리뷰 등록 중복 방지
        if(reviewRepository.existsReviewPostByMemberAndPlatform(findMember.getMemberId(), findPlatform.getPlatformId())){
            throw new BusinessLogicException(ExceptionCode.ALREADY_EXISTS);
        }

        review.setMember(findMember);
        review.setPlatform(findPlatform);
        Review savedReview = reviewRepository.save(review);

        //리뷰 개수, 별점 업데이트
        //해당 플랫폼에 리뷰 추가 및 리뷰 개수, 별점 업데이트
        findPlatform.getReviews().add(review);
        updatePlatformReviewStats(findPlatform);

        return savedReview;
    }

    public Review updateReview(Long platformId, Review review, Long memberId){
        Member findMember = memberService.findVerifiedMember(memberId);
        Platform findPlatform = platformService.findVerifiedPlatform(platformId);
        Review findreview = findVerifiedReview(review.getReviewId());

        //삭제된 리뷰인지 검증
        if(findreview.getReviewStatus().equals(Review.ReviewStatus.REVIEW_DELETE)){
            throw new BusinessLogicException(ExceptionCode.NOT_FOUND);
        }

        //작성자 인지 검증
        isReviewOwner(findreview, findMember.getMemberId());

        Optional.ofNullable(review.getContent())
                .ifPresent(content -> findreview.setContent(content));
        Optional.of(review.getRating())
                .ifPresent(rating -> findreview.setRating(rating));

        Review saveReview = reviewRepository.save(findreview);
        updatePlatformAverageRatingOnly(findPlatform);
        return saveReview;
    }

    public Page<Review> findReviews(int page, int size, Long platformId){
        Platform findPlatform = platformService.findVerifiedPlatform(platformId);
        Pageable pageable = PageRequest.of(page, size);

        return reviewRepository.findActiveReviewsByPlatform(findPlatform, pageable);
    }

    public Page<Review> findMyReviews(int page, int size, Long memberId){
        Member findMember = memberService.findVerifiedMember(memberId);
        Pageable pageable = PageRequest.of(page, size);

        return reviewRepository.findActiveReviewsAndMember(findMember, pageable);
    }

    public void deleteReview(Long platformId, Long reviewId, Long memberId){
        memberService.findVerifiedMember(memberId);
        Platform findPlatform = platformService.findVerifiedPlatform(platformId);
        Review review = findVerifiedReview(reviewId);
        isReviewOwner(review, memberId);

        if(review.getReviewStatus().equals(Review.ReviewStatus.REVIEW_DELETE)){
            throw new BusinessLogicException(ExceptionCode.ALREADY_DELETED);
        }

        review.setReviewStatus(Review.ReviewStatus.REVIEW_DELETE);
        reviewRepository.save(review);

        //리뷰 개수, 별점 업데이트
        updatePlatformReviewStats(findPlatform);
    }

    //추천 등록
    public void recommendReview(Long reviewId, Long memberId){
        Review review = findVerifiedReview(reviewId);
        Member member = memberService.findVerifiedMember(memberId);

        //추천 중복 검증
        if (reviewRecommendRepository.existsByReviewAndMember(review, member)) {
            throw new BusinessLogicException(ExceptionCode.ALREADY_EXISTS);
        }

        ReviewRecommend recommend = new ReviewRecommend();
        recommend.setReview(review);
        recommend.setMember(member);
        reviewRecommendRepository.save(recommend);

        int count = reviewRecommendRepository.countByReview(review);
        review.setRecommendCount(count);
    }

    public void cancelRecommend(Long reviewId, Long memberId) {
        Review review = findVerifiedReview(reviewId);
        Member member = memberService.findVerifiedMember(memberId);

        ReviewRecommend recommend = reviewRecommendRepository.findByReviewAndMember(review, member).orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.ALREADY_DELETED));

        reviewRecommendRepository.delete(recommend);

        // 추천 수 업데이트
        int count = reviewRecommendRepository.countByReview(review);
        review.setRecommendCount(count);
        reviewRepository.save(review);
    }

    //해당 플랫폼에 평균 별점, 리뷰 수 갱신 메서드
    public void updatePlatformReviewStats(Platform platform){
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
