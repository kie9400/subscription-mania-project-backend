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
}
