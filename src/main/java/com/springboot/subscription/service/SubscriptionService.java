package com.springboot.subscription.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.notification.service.NotificationService;
import com.springboot.platform.entity.Platform;
import com.springboot.platform.service.PlatformService;
import com.springboot.plan.service.SubsPlanService;
import com.springboot.subscription.entity.Subscription;
import com.springboot.subscription.repository.SubscriptionRepository;
import com.springboot.plan.entity.SubsPlan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final MemberService memberService;
    private final PlatformService platformService;
    private final SubsPlanService subscriptionService;
    private final NotificationService notificationService;

    //결제일 갱신을 위한 스케줄러 메서드
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정마다 체크
    public void updateNextPaymentDates() {
        LocalDate today = LocalDate.now();

        // 오늘 결제일인 구독 내역 조회
        List<Subscription> dueSubscriptions = subscriptionRepository.findAllByNextPaymentDate(today);

        // 오늘 결제일인 구독 내역을 주기에 맞게 결제일 갱신
        for (Subscription sub : dueSubscriptions) {
            if ("연".equals(sub.getSubsPlan().getBillingCycle())) {
                sub.setNextPaymentDate(sub.getNextPaymentDate().plusYears(1));
            } else {
                sub.setNextPaymentDate(sub.getNextPaymentDate().plusMonths(1));
            }

            //갱신일에 맞게 알림도 예약
            notificationService.scheduleNotification(sub);
        }

        subscriptionRepository.saveAll(dueSubscriptions);
        log.info("[스케줄러] {}건의 구독 결제일 갱신 완료", dueSubscriptions.size());
    }

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

        LocalDate baseDate = subscription.getSubscriptionAt();
        LocalDate nextPaymentDate = baseDate;
        subscription.setSubsPlan(plan);

        // 현재 날짜보다 뒤에 있는 날짜가 나올 때까지 반복한다
        // 예를들어 2025년 1월 1일에 구독 시작 했다면 다음 결제일은 5월 1일(today)
        while (!nextPaymentDate.isAfter(LocalDate.now())) {
            nextPaymentDate = "연".equals(subscription.getSubsPlan().getBillingCycle())
                    ? nextPaymentDate.plusYears(1)
                    : nextPaymentDate.plusMonths(1);
        }
        subscription.setNextPaymentDate(nextPaymentDate);

        //구독을 등록할때 그에 맞는 결제전 알람도 등록해야 한다.
        notificationService.scheduleNotification(subscription);

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
                .ifPresent(findSubs::setSubscriptionAt);

        validateSubsStartDate(platform, findSubs);
        LocalDate nextPaymentDate = findSubs.getSubscriptionAt();

        // 현재 날짜보다 뒤에 있는 날짜가 나올 때까지 반복한다
        // 예를들어 2025년 1월 1일에 구독 시작 했다면 다음 결제일은 5월 1일(today)
        while (!nextPaymentDate.isAfter(LocalDate.now())) {
            nextPaymentDate = "연".equals(findSubs.getSubsPlan().getBillingCycle())
                    ? nextPaymentDate.plusYears(1)
                    : nextPaymentDate.plusMonths(1);
        }
        findSubs.setNextPaymentDate(nextPaymentDate);
        //이전 구독 알람을 삭제한다.
        notificationService.cancleNotifications(findSubs);

        //구독을 등록할때 그에 맞는 결제전 알람도 등록해야 한다.
        notificationService.scheduleNotification(findSubs);

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

    // 사용자가 카테고리별로 구독내역을 조회하기 위한 메서드
    @Transactional(readOnly = true)
    public List<Subscription> findSubscriptionsWithPlanAndPlatform(Long memberId) {
        return subscriptionRepository.findAllByMemberIdWithPlatformAndPlan(memberId);
    }

    // 구독 상세 페이지 (구독 단일 내역 조회) 메서드
    @Transactional(readOnly = true)
    public Subscription findSubscription(Long subscriptionId, Long memberId){
        Member member = memberService.findVerifiedMember(memberId);
        Subscription subs = findVerifiedSubs(subscriptionId);

        isSubsOwner(subs, member.getMemberId());

        if(subs.getSubsStatus().equals(Subscription.SubsStatus.SUBSCRIBE_CANCEL)){
            throw new BusinessLogicException(ExceptionCode.NOT_FOUND);
        }

        return subs;
    }

    //구독 시작일이 플랫폼 서비스 일자보다 앞서있는지 검증
    public void validateSubsStartDate(Platform platform, Subscription subscription){
        if(subscription.getSubscriptionAt().isBefore(platform.getServiceAt())){
            throw new BusinessLogicException(ExceptionCode.INVALID_SUBSCRIPTION_DATE);
        }
    }

    //구독이 존재하는지 검증하는 메서드
    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public boolean findSubsWithMemberAndPlatform(Long memberId, Long platformId){
        return subscriptionRepository.existsActiveSubscriptionByPlatform(memberId, platformId);
    }
}
