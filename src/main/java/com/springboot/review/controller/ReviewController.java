package com.springboot.review.controller;

import com.springboot.dto.MultiResponseDto;
import com.springboot.member.entity.Member;
import com.springboot.review.dto.ReviewDto;
import com.springboot.review.entity.Review;
import com.springboot.review.mapper.ReviewMapper;
import com.springboot.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/platforms/{platform-id}/reviews")
public class ReviewController {
    private final ReviewMapper mapper;
    private final ReviewService reviewService;

    public ReviewController(ReviewMapper mapper, ReviewService reviewService) {
        this.mapper = mapper;
        this.reviewService = reviewService;
    }

    @Operation(summary = "리뷰 등록", description = "리뷰를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "리뷰 등록 완료"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 입니다.(로그인 상태아님)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자 입니다.\"}"))),
            @ApiResponse(responseCode = "400", description = "리뷰 유효성 검증 실패",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 400, \"message\": \"유효성 검증 통과 실패\"}")))
    })
    @PostMapping
    public ResponseEntity postReview(@PathVariable("platform-id") long platformId,
                                      @Valid @RequestBody ReviewDto.Post reviewPostDto,
                                      @Parameter(hidden = true) @AuthenticationPrincipal Member member){
        Review review = reviewService.createReview(platformId, mapper.reviewPostDtoToReview(reviewPostDto), member.getMemberId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity getComments(@PathVariable("platform-id") long platformId,
                                      @Positive @RequestParam int page,
                                      @Positive @RequestParam int size,
                                      @Parameter(hidden = true) @AuthenticationPrincipal Member member){
        Page<Review> reviewPage = reviewService.findReviews(page - 1, size, member.getMemberId(), platformId);
        List<Review> reviews = reviewPage.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>
                (mapper.reviewToReviewResponseDtos(reviews),reviewPage),HttpStatus.OK);
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "리뷰 등록 완료"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 입니다.(로그인 상태아님)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자 입니다.\"}"))),
            @ApiResponse(responseCode = "404", description = "해당 리뷰가 존재하지 않습니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 404, \"message\": \"해당 리뷰가 존재하지 않습니다.\"}")))
    })
    @DeleteMapping("{review-id}")
    public ResponseEntity deleteReview(@PathVariable("platform-id") long platformId,
                                       @PathVariable("review-id") long reviewId,
                                       @Parameter(hidden = true) @AuthenticationPrincipal Member member){
        reviewService.deleteReview(platformId, reviewId, member.getMemberId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
