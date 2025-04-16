package com.springboot.member.service;

import com.springboot.member.entity.Member;
import com.springboot.review.entity.Review;
import com.springboot.review.service.ReviewService;
import com.springboot.subscription.entity.Subscription;
import com.springboot.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class MypageService {
    private final MemberService memberService;
    private final SubscriptionService subscriptionService;
    private final ReviewService reviewService;

    public Member findMyInfo(Member member){
        return memberService.findVerifiedMember(member.getMemberId());
    }

    public List<Subscription> findMySubsList(Member member){
        return subscriptionService.findSubscriptionsWithPlanAndPlatform(member.getMemberId());
    }

    public Page<Review> findMyReviewList(int page, int size, Member member){
        return reviewService.findMyReviews(page, size, member.getMemberId());
    }
}
