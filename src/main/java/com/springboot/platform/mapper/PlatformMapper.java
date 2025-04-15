package com.springboot.platform.mapper;

import com.springboot.subsplan.dto.PlanDto;
import com.springboot.platform.dto.PlatformDto;
import com.springboot.platform.entity.Platform;
import org.mapstruct.Mapper;

import java.util.List;
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
