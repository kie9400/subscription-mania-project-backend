package com.springboot.notification.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.mail.service.MailService;
import com.springboot.member.entity.Member;
import com.springboot.notification.entity.Notification;
import com.springboot.notification.repository.NotificaitonRepository;
import com.springboot.subscription.entity.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificaitonRepository notificationRepository;
    private final MailService mailService;

    public void scheduleNotification(Subscription subscription) {
        Notification notification = new Notification();
        notification.setSubscription(subscription);
        notification.setNotifyDaysBefore(3);

        LocalDate scheduledAt = LocalDate.from(subscription.getNextPaymentDate()
                .minusDays(3)
                .atStartOfDay());

        notification.setScheduledAt(scheduledAt);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setSent(false);

        notificationRepository.save(notification);
    }

    public void cancleNotifications(Subscription subscription){
        Optional<Notification> optionalNotification = notificationRepository.findBySubscription_SubscriptionId(subscription.getSubscriptionId());

        Notification notification = optionalNotification.orElseThrow(()
                -> new BusinessLogicException(ExceptionCode.NOT_FOUND));

        //지금은 db에서 삭제하지만 이후 데이터를 남기려면 is_sent가 아닌 상태 컬럼을 하나 만들어야 될것으로 예상된다.
        notificationRepository.delete(notification);
    }

    @Scheduled(cron = "0 0 0/12 * * *")
    public void sendScheduledNotifications() {
        LocalDate today = LocalDate.now();

        List<Notification> notifications = notificationRepository.findByScheduledAtBeforeAndIsSentFalse(today);

        //사용자 별로 구독내역(List)을 그룹화한다.
        Map<Member, List<Subscription>> subsGroup = notifications.stream()
                .collect(Collectors.groupingBy(
                        //멤버를 기준으로 그룹화
                        noti -> noti.getSubscription().getMember(),
                        //각 알림에서 구독만 꺼내어 리스트로 묶는다.
                        Collectors.mapping(Notification::getSubscription, Collectors.toList())
                ));

        // 사용자별로 알림 전송
        for (Map.Entry<Member, List<Subscription>> entry : subsGroup.entrySet()) {
            Member member = entry.getKey();
            List<Subscription> subs = entry.getValue();

            try {
                mailService.sendReminderEmail(member.getEmail(), subs); // 💌 사용자당 1번 발송
                // 전송 완료한 알림들 모두 true 처리
                notifications.stream()
                        .filter(noti -> subs.contains(noti.getSubscription()))
                        .forEach(noti -> noti.setSent(true));
            } catch (Exception e) {
                // 전송 실패 시 isSent 유지
            }
        }

        notificationRepository.saveAll(notifications);
    }
}
