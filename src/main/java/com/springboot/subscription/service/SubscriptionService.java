package com.springboot.subscription.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.platform.entity.Platform;
import com.springboot.platform.service.PlatformService;
import com.springboot.platform.service.SubsPlanService;
import com.springboot.subscription.entity.Subscription;
import com.springboot.subscription.repository.SubscriptionRepository;
import com.springboot.platform.entity.SubsPlan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final MemberService memberService;
    private final PlatformService platformService;
    private final SubsPlanService subscriptionService;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, MemberService memberService, PlatformService platformService, SubsPlanService subscriptionService) {
        this.subscriptionRepository = subscriptionRepository;
        this.memberService = memberService;
        this.platformService = platformService;
        this.subscriptionService = subscriptionService;
    }

    public Subscription createSubs(Subscription subscription, Long memberId, Long platformId, Long subsPlanId){
        Platform platform =platformService.findVerifiedPlatform(platformId);
        Member member = memberService.findVerifiedMember(memberId);
        SubsPlan plan = subscriptionService.VerifiedSubsPlan(subsPlanId);

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
}
