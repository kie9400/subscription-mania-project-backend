package com.springboot.subscription.mapper;

import com.springboot.subscription.dto.SubscriptionDto;
import com.springboot.subscription.entity.Subscription;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    Subscription subsPostDtoToSubs(SubscriptionDto.Post requestBody);
    Subscription subsPatchDtoToSubs(SubscriptionDto.Patch requestBody);

    @Mapping(target = "platformName", source = "subsPlan.platform.platformName")
    @Mapping(target = "platformImage", source = "subsPlan.platform.platformImage")
    @Mapping(target = "subsPlanName", source = "subsPlan.planName")
    @Mapping(target = "price", source = "subsPlan.price")
    @Mapping(target = "subscriptionStartAt", source = "subscriptionAt")
    SubscriptionDto.Response subsToSubsResponseDto(Subscription subscription);

    // 자동 매핑 이후 계산, 커스텀마이징이 필요한 로직을 수동 매핑으로 작성할 수 있게한다.
    @AfterMapping
    default void setNextPaymentDate(Subscription subscription,
                                    @MappingTarget SubscriptionDto.Response response){
        LocalDate startAt = subscription.getSubscriptionAt();
        String billingCycle = subscription.getBillingCycle();
        LocalDate nextPaymentDate;

        if(billingCycle.equals("1년")){
            nextPaymentDate = startAt.plusYears(1);
        }else{
            nextPaymentDate = startAt.plusMonths(1);
        }
        response.setNextPaymentDate(nextPaymentDate);
    }
}
