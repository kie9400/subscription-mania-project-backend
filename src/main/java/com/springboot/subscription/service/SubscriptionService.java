package com.springboot.subscription.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.platform.entity.Platform;
import com.springboot.platform.service.PlatformService;
import com.springboot.plan.service.SubsPlanService;
import com.springboot.subscription.entity.Subscription;
import com.springboot.subscription.repository.SubscriptionRepository;
import com.springboot.plan.entity.SubsPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final MemberService memberService;
    private final PlatformService platformService;
    private final SubsPlanService subscriptionService;

    public Subscription createSubs(Subscription subscription, Long memberId, Long platformId, Long subsPlanId){
        Platform platform =platformService.findVerifiedPlatform(platformId);
        Member member = memberService.findVerifiedMember(memberId);
        SubsPlan plan = subscriptionService.VerifiedSubsPlan(subsPlanId);

        validateSubsStartDate(platform, subscription);

        //이 플랜이 해당 플랫폼에 속하는 플랜인지 검증
        if (!plan.getPlatform().getPlatformId().equals(platform.getPlatformId())){
            throw new BusinessLogicException(ExceptionCode.NOT_FOUND);
        }

        //같은 플랫폼에 중복 구독 안되게 설정
        if (subscriptionRepository.existsSubsStatusByMemberAndPlatform(member.getMemberId(), platform.getPlatformId())){
            throw new BusinessLogicException(ExceptionCode.ALREADY_EXISTS);
        }

        subscription.setSubsPlan(plan);
        subscription.setMember(member);
        return subscriptionRepository.save(subscription);
    }

    public Subscription updateSubs(Subscription subscription, Long memberId, Long platformId, Long subsPlanId){
        Platform platform =platformService.findVerifiedPlatform(platformId);
        Member member = memberService.findVerifiedMember(memberId);
        SubsPlan plan = subscriptionService.VerifiedSubsPlan(subsPlanId);
        Subscription findSubs = findVerifiedSubs(subscription.getSubscriptionId());

        if(findSubs.getSubsStatus().equals(Subscription.SubsStatus.SUBSCRIBE_CANCEL)){
            throw new BusinessLogicException(ExceptionCode.NOT_FOUND);
        }

        isSubsOwner(findSubs, memberId);

        //이 플랜이 해당 플랫폼에 속하는 플랜인지 검증
        if (!plan.getPlatform().getPlatformId().equals(platform.getPlatformId())){
            throw new BusinessLogicException(ExceptionCode.NOT_FOUND);
        }

        //수정할 구독을 제외하고 -> 해당 플랫폼에 중복 구독이 있는지 검증
        if (subscriptionRepository.existsByMemberAndPlatformAndNotThisSubscription(member.getMemberId()
                , platform.getPlatformId(), findSubs.getSubscriptionId())) {
            throw new BusinessLogicException(ExceptionCode.ALREADY_EXISTS);
        }

        findSubs.setSubsPlan(plan);
        Optional.ofNullable(subscription.getSubscriptionAt())
                .ifPresent(subsAt -> findSubs.setSubscriptionAt(subsAt));
        Optional.ofNullable(subscription.getBillingCycle())
                .ifPresent(cycle -> findSubs.setBillingCycle(cycle));

        validateSubsStartDate(platform, findSubs);

        return subscriptionRepository.save(findSubs);
    }

    public void deleteSubs(Long subscriptionId, Long memberId){
        Member member = memberService.findVerifiedMember(memberId);
        Subscription subs = findVerifiedSubs(subscriptionId);

        isSubsOwner(subs, member.getMemberId());

        if (subs.getSubsStatus().equals(Subscription.SubsStatus.SUBSCRIBE_CANCEL)){
            throw new BusinessLogicException(ExceptionCode.ALREADY_DELETED);
        }

        subs.setSubsStatus(Subscription.SubsStatus.SUBSCRIBE_CANCEL);
        subscriptionRepository.save(subs);
    }

    //구독 시작일이 플랫폼 서비스 일자보다 앞서있는지 검증
    public void validateSubsStartDate(Platform platform, Subscription subscription){
        if(subscription.getSubscriptionAt().isBefore(platform.getServiceAt())){
            throw new BusinessLogicException(ExceptionCode.INVALID_SUBSCRIPTION_DATE);
        }
    }

    //구독이 존재하는지 검증하는 메서드
    public Subscription findVerifiedSubs(long subscriptionId){
        Optional<Subscription> optionalSubscription = subscriptionRepository.findById(subscriptionId);
        Subscription subscription = optionalSubscription.orElseThrow(
                ()-> new BusinessLogicException(ExceptionCode.NOT_FOUND));

        return subscription;
    }

    //이 구독을 등록한 구독자(사용자)가 맞는지 검증
    public void isSubsOwner(Subscription subscription, long memberId){
        memberService.isAuthenticatedMember(subscription.getMember().getMemberId(), memberId);
    }

    // 사용자가 카테고리별로 구독내역을 조회하기 위한 메서드
    public List<Subscription> findSubscriptionsWithPlanAndPlatform(Long memberId) {
        return subscriptionRepository.findAllByMemberIdWithPlatformAndPlan(memberId);
    }

}
