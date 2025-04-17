package com.springboot.review.controller;

import com.springboot.dto.MultiResponseDto;
import com.springboot.member.entity.Member;
import com.springboot.review.dto.ReviewDto;
import com.springboot.review.entity.Review;
import com.springboot.review.mapper.ReviewMapper;
import com.springboot.review.service.ReviewService;
import com.springboot.subscription.dto.SubscriptionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@Tag(name = "리뷰 API", description = "리뷰 관련 API")
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
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 400, \"message\": \"유효성 검증 통과 실패\"}"))),
            @ApiResponse(responseCode = "409", description = "이미 리뷰를 등록하셨습니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 409, \"message\": \"이미 리뷰를 등록하셨습니다.\"}")))
    })
    @PostMapping
    public ResponseEntity postReview(@PathVariable("platform-id") long platformId,
                                      @Valid @RequestBody ReviewDto.Post reviewPostDto,
                                      @Parameter(hidden = true) @AuthenticationPrincipal Member member){
        Review review = reviewService.createReview(platformId, mapper.reviewPostDtoToReview(reviewPostDto), member.getMemberId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "리뷰 수정", description = "리뷰를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "리뷰 수정 완료"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 입니다.(로그인 상태아님)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자 입니다.\"}"))),
            @ApiResponse(responseCode = "400", description = "리뷰 유효성 검증 실패",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 400, \"message\": \"유효성 검증 통과 실패\"}"))),
            @ApiResponse(responseCode = "409", description = "해당 리뷰는 삭제된 상태입니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 409, \"message\": \"해당 리뷰는 삭제된 상태입니다.\"}")))

    })
    @PatchMapping("{review-id}")
    public ResponseEntity patchReviews(@PathVariable("platform-id") long platformId,
                                       @Valid @RequestBody ReviewDto.Patch reviewPatchDto,
                                       @PathVariable("review-id") long reviewId,
                                       @Parameter(hidden = true) @AuthenticationPrincipal Member member){
        reviewPatchDto.setReviewId(reviewId);
        Review review = reviewService.updateReview(platformId, mapper.reviewPatchDtoToReview(reviewPatchDto), member.getMemberId());


        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Operation(summary = "리뷰 전체 조회", description = "리뷰를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "리뷰 조회 완료",
                    content = @Content(
                            mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ReviewDto.Response.class))
                    )),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 입니다.(로그인 상태아님)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자 입니다.\"}")))
    })
    @GetMapping
    public ResponseEntity getReviews(@PathVariable("platform-id") long platformId,
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
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 404, \"message\": \"해당 리뷰가 존재하지 않습니다.\"}"))),
            @ApiResponse(responseCode = "409", description = "해당 리뷰는 삭제된 상태입니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 409, \"message\": \"해당 리뷰는 삭제된 상태입니다.\"}")))
    })
    @DeleteMapping("{review-id}")
    public ResponseEntity deleteReview(@PathVariable("platform-id") long platformId,
                                       @PathVariable("review-id") long reviewId,
                                       @Parameter(hidden = true) @AuthenticationPrincipal Member member){
        reviewService.deleteReview(platformId, reviewId, member.getMemberId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "리뷰 추천", description = "리뷰를 추천합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "리뷰 추천 완료"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 입니다.(로그인 상태아님)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자 입니다.\"}"))),
            @ApiResponse(responseCode = "404", description = "해당 리뷰가 존재하지 않습니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 404, \"message\": \"해당 리뷰가 존재하지 않습니다.\"}"))),
            @ApiResponse(responseCode = "409", description = "해당 리뷰에는 이미 추천을 했습니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 409, \"message\": \"해당 리뷰에는 이미 추천을 했습니다.\"}")))
    })
    @PostMapping("{review-id}/recommend")
    public ResponseEntity postRecommend(@PathVariable("platform-id") long platformId,
                                        @PathVariable("review-id") long reviewId,
                                        @Parameter(hidden = true) @AuthenticationPrincipal Member member){
        reviewService.recommendReview(reviewId, member.getMemberId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "리뷰 추천 취소", description = "리뷰 추천을 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "추천 취소 완료"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 입니다.(로그인 상태아님)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자 입니다.\"}"))),
            @ApiResponse(responseCode = "404", description = "해당 리뷰가 존재하지 않습니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 404, \"message\": \"해당 리뷰가 존재하지 않습니다.\"}")))
    })
    @DeleteMapping("{review-id}/recommend")
    public ResponseEntity deleteRecommend(@PathVariable("platform-id") long platformId,
                                        @PathVariable("review-id") long reviewId,
                                        @Parameter(hidden = true) @AuthenticationPrincipal Member member){
        reviewService.cancelRecommend(reviewId, member.getMemberId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
