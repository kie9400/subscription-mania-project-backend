package com.springboot.platform.mapper;

import com.springboot.plan.dto.PlanDto;
import com.springboot.platform.dto.PlatformDto;
import com.springboot.platform.entity.Platform;
import org.mapstruct.Mapper;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PlatformMapper {
    default PlatformDto.Response platformToPlatformResponse(Platform platform) {
        PlatformDto.Response.ResponseBuilder builder = PlatformDto.Response.builder()
                .platformId(platform.getPlatformId())
                .platformName(platform.getPlatformName())
                .categoryName(platform.getCategory().getCategoryName())
                .platformDescription(platform.getPlatformDescription())
                .platformImage(platform.getPlatformImage())
                .reviewCount(platform.getReviewCount())
                .ratingAvg(platform.getRatingAvg())
                .serviceAt(platform.getServiceAt())
                .plans(platform.getSubsPlans().stream()
                        .map(subsPlan -> PlanDto.Response.builder()
                                .subsPlanId(subsPlan.getSubsPlanId())
                                .planName(subsPlan.getPlanName())
                                .price(subsPlan.getPrice())
                                .build())
                        .collect(Collectors.toList())
                );
        return builder.build();
    }
}
