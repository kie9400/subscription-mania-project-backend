package com.springboot.platform.mapper;

import com.springboot.plan.dto.PlanDto;
import com.springboot.plan.entity.SubsPlan;
import com.springboot.platform.dto.PlatformDto;
import com.springboot.platform.entity.Platform;
import org.mapstruct.Mapper;
import org.springframework.security.core.parameters.P;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface PlatformMapper {
    default Platform platformPostToPlatform(PlatformDto.Post postDto){
        Platform platform = new Platform();
        platform.setPlatformName(postDto.getPlatformName());
        platform.setPlatformDescription(postDto.getPlatformDescription());
        platform.setServiceAt(postDto.getServiceAt());

        List<SubsPlan> subsPlans = postDto.getSubsPlans().stream()
                .map(subsPlanDto -> {
                    SubsPlan subsPlan = new SubsPlan();
                    subsPlan.setPlatform(platform);
                    subsPlan.setPlanName(subsPlanDto.getPlanName());
                    subsPlan.setPrice(subsPlanDto.getPrice());
                    subsPlan.setBillingCycle(subsPlanDto.getBillingCycle());
                    return subsPlan;
                }).collect(Collectors.toList());
        platform.setSubsPlans(subsPlans);
        return platform;
    }

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
                                .billingCycle(subsPlan.getBillingCycle())
                                .build())
                        .collect(Collectors.toList())
                );
        return builder.build();
    }

    //플랫폼 전체 조회 (단일) mapper
    default PlatformDto.AllResponse toAllResponse(Platform platform) {
        return PlatformDto.AllResponse.builder()
                .platformId(platform.getPlatformId())
                .platformName(platform.getPlatformName())
                .platformImage(platform.getPlatformImage())
                .ratingAvg(platform.getRatingAvg())
                .build();
    }

    //플랫폼 전체 조회(List) mapper
    default List<PlatformDto.AllResponse> toAllResponseList(List<Platform> platforms) {
        return platforms.stream()
                .map(this::toAllResponse)
                .collect(Collectors.toList());
    }
}
