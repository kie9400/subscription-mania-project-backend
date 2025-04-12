package com.springboot.notification.entity;

import com.springboot.subscription.entity.Subscription;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    //알림 기준일 = 3일로 할 예정
    @Column(nullable = false)
    private String notifyDaysBefore;

    //알림 전송 시간
    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    //알림 생성 시간
    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    //알림 전송 여부
    @Column(nullable = false)
    private boolean isSent = false;

    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;
}