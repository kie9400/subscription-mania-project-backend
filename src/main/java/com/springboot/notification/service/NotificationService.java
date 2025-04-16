package com.springboot.notification.service;

import com.springboot.mail.service.MailService;
import com.springboot.notification.entity.Notification;
import com.springboot.notification.repository.NotificaitonRepository;
import com.springboot.subscription.entity.Subscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    @Scheduled(cron = "0 0 0 * * *")
    public void sendScheduledNotifications() {
        LocalDate today = LocalDate.now();

        List<Notification> notifications = notificationRepository.findByScheduledAtBeforeAndIsSentFalse(today);

        for (Notification noti : notifications) {
            try {
                Subscription sub = noti.getSubscription();
                String email = sub.getMember().getEmail();

                mailService.sendReminderEmail(email, sub);
                noti.setSent(true);
            } catch (Exception e) {
                // 실패한 건 isSent 그대로 false 유지
            }
        }

        notificationRepository.saveAll(notifications);
    }
}
