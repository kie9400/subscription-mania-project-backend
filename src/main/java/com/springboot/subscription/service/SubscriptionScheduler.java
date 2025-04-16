package com.springboot.subscription.service;

import com.springboot.subscription.entity.Subscription;
import com.springboot.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class SubscriptionScheduler {
    private final SubscriptionRepository subscriptionRepository;
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정
    public void updateNextPaymentDates() {
        LocalDate today = LocalDate.now();

        // 1. 오늘 결제일인 구독 내역 조회
        List<Subscription> dueSubscriptions = subscriptionRepository.findAllByNextPaymentDate(today);

        // 2. 결제일 갱신
        for (Subscription sub : dueSubscriptions) {
            if ("1년".equals(sub.getBillingCycle())) {
                sub.setNextPaymentDate(sub.getNextPaymentDate().plusYears(1));
            } else {
                sub.setNextPaymentDate(sub.getNextPaymentDate().plusMonths(1));
            }
        }

        subscriptionRepository.saveAll(dueSubscriptions);
        log.info("[스케줄러] {}건의 구독 결제일 갱신 완료", dueSubscriptions.size());
    }
}
