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

    @Mapping(target = "platformId", source = "subsPlan.platform.platformId")
    @Mapping(target = "platformName", source = "subsPlan.platform.platformName")
    @Mapping(target = "platformImage", source = "subsPlan.platform.platformImage")
    @Mapping(target = "subsPlanName", source = "subsPlan.planName")
    @Mapping(target = "billingCycle", source = "subsPlan.billingCycle")
    @Mapping(target = "price", source = "subsPlan.price")
    @Mapping(target = "subscriptionStartAt", source = "subscriptionAt")
    SubscriptionDto.Response subsToSubsResponseDto(Subscription subscription);
}
