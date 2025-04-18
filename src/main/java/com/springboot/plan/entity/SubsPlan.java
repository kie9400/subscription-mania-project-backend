package com.springboot.plan.entity;

import com.springboot.platform.entity.Platform;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class SubsPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long subsPlanId;

    @Column(nullable = false)
    private String planName;

    @Column(nullable = false)
    private int price;

    //결제 주기
    @Column(nullable = false)
    private String billingCycle;

    @ManyToOne
    @JoinColumn(name = "platform_id")
    private Platform platform;

    public void setPlatform(Platform platform) {
        this.platform = platform;
        if (!platform.getSubsPlans().contains(this)){
            platform.setSubsPlan(this);
        }
    }
}
