package com.springboot.home.dto;

import com.springboot.category.dto.CategoryDto;
import com.springboot.platform.dto.PlatformDto;
import com.springboot.subscription.dto.SubscriptionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MainDto {
    List<CategoryDto.Response> categories;
    List<PlatformDto.AllResponse> platforms;
}
