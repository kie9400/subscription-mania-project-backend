package com.springboot.review.mapper;

import com.springboot.review.dto.ReviewDto;
import com.springboot.review.entity.Review;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    Review reviewPostDtoToReview(ReviewDto.Post postDto);
}
