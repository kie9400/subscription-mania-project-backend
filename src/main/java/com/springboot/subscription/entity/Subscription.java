package com.springboot.subscription.entity;

import com.springboot.audit.BaseEntity;
import com.springboot.member.entity.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Subscription extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subscriptionId;

    @Column(nullable = false)
    private String billingCycle;

    @Column(name = "subscription_at", updatable = false, nullable = false)
    private LocalDateTime subscriptionAt;

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
