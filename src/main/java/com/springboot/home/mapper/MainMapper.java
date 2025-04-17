package com.springboot.home.mapper;

import com.springboot.category.dto.CategoryDto;
import com.springboot.home.dto.MainDto;
import com.springboot.platform.dto.PlatformDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MainMapper {
    default MainDto responseDtoToMainDto(List<CategoryDto.Response> categories, List<PlatformDto.AllResponse> platforms) {
        MainDto dto = new MainDto();
        dto.setCategories(categories);
        dto.setPlatforms(platforms);
        return dto;
    }
}
