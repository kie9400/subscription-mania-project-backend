package com.springboot.subscription.entity;

import com.springboot.audit.BaseEntity;
import com.springboot.member.entity.Member;
import com.springboot.plan.entity.SubsPlan;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Subscription extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subscriptionId;

    //구독 시작일
    @Column(name = "subscription_at", nullable = false)
    private LocalDate subscriptionAt;

    //다음 결제일
    @Column(nullable = false)
    private LocalDate nextPaymentDate;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private SubsStatus subsStatus = SubsStatus.SUBSCRIBE_ACTIVE;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "subs_plan_id")
    private SubsPlan subsPlan;

    public void setMember(Member member) {
        this.member = member;
        if (!member.getSubscriptions().contains(this)) {
            member.setSubscription(this);
        }
    }

    public enum SubsStatus {
        SUBSCRIBE_ACTIVE("구독 중"),
        SUBSCRIBE_CANCEL("구독 취소");

        @Getter
        private String status;

        SubsStatus(String status) {
            this.status = status;
        }
    }
}
