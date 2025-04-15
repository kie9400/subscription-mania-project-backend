package com.springboot.subscription.mapper;

import com.springboot.subscription.dto.SubscriptionDto;
import com.springboot.subscription.entity.Subscription;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    Subscription subsPostDtoToSubs(SubscriptionDto.Post requestBody);
}
