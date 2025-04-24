package com.springboot.notification.repository;

import com.springboot.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificaitonRepository extends JpaRepository<Notification, Long> {
    //전송시간이 지났고(전송 시간이 < tdoay) 아직 안보낸 알림만 조회(isSnet가 false)
    List<Notification> findByScheduledAtBeforeAndIsSentFalse(LocalDate today);

    Optional<Notification> findBySubscription_SubscriptionId(Long subscriptionId);
}
