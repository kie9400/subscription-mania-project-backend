package com.springboot.review.mapper;

import com.springboot.review.dto.ReviewDto;
import com.springboot.review.entity.Review;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    Review reviewPostDtoToReview(ReviewDto.Post postDto);
    List<ReviewDto.Response> reviewToReviewResponseDtos(List<Review>reviews);

    default ReviewDto.Response reviewToReviewResponseDto(Review review){
        ReviewDto.Response.ResponseBuilder builder = ReviewDto.Response.builder()
                .reviewId(review.getReviewId())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .memberId(review.getMember().getMemberId())
                .memberName(review.getMember().getName())
                .rating(review.getRating());
        return builder.build();
    }
}
