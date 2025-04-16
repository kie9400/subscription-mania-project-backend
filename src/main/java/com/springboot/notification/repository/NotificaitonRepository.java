package com.springboot.notification.repository;

import com.springboot.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificaitonRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByScheduledAtBeforeAndIsSentFalse(LocalDateTime now);
}
