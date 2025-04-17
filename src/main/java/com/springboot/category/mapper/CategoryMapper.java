package com.springboot.category.mapper;

import com.springboot.category.dto.CategoryDto;
import com.springboot.category.entity.Category;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    List<CategoryDto.Response> categoryToCategoryResponseDtos(List<Category> categories);
}
