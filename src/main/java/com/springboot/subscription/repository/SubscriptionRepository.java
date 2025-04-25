package com.springboot.subscription.repository;

import com.springboot.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long>, SubscriptionRepositoryCustom {
    List<Subscription> findAllByNextPaymentDate(LocalDate date);
}
