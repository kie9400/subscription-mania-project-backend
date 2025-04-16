package com.springboot.member.controller;

import com.springboot.dto.MultiResponseDto;
import com.springboot.dto.SingleResponseDto;
import com.springboot.member.dto.MemberDto;
import com.springboot.member.dto.MySubsResponseDto;
import com.springboot.member.entity.Member;
import com.springboot.member.mapper.MemberMapper;
import com.springboot.member.service.MemberService;
import com.springboot.member.service.MypageService;
import com.springboot.review.entity.Review;
import com.springboot.review.service.ReviewService;
import com.springboot.subscription.entity.Subscription;
import com.springboot.subscription.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {
    private final MypageService mypageService;
    private final MemberMapper mapper;

    @Operation(summary = "마이페이지", description = "마이페이지 진입시 1회 사용자 정보 호출(사이드바)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "마이페이지 조회 완료"),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    @GetMapping()
    public ResponseEntity getMyPage(@Parameter(hidden = true) @AuthenticationPrincipal Member member){
        Member findMember = mypageService.findMyInfo(member);
        MemberDto.MyPageResponse response = mapper.memberToMyPage(findMember);

        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }

    @Operation(summary = "마이페이지(내 정보 조회)", description = "내 정보 조회 (마이페이지 디폴트 페이지)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "내 정보 조회 완료"),
            @ApiResponse(responseCode = "404", description = "Member not found")
    })
    @GetMapping("/info")
    public ResponseEntity getMyInfo(@Parameter(hidden = true) @AuthenticationPrincipal Member member){
        Member findMember = mypageService.findMyInfo(member);
        MemberDto.MyInfoResponse response = mapper.memberToMyInfo(findMember);

        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }

    @GetMapping("/subs")
    public ResponseEntity getMySubs(@Parameter(hidden = true) @AuthenticationPrincipal Member member){
        List<Subscription> subscriptions = mypageService.findMySubsList(member);

        MySubsResponseDto responseDto = mapper.memberToMySubsResponse(subscriptions);

        return ResponseEntity.ok(new SingleResponseDto<>(responseDto));
    }

    @GetMapping("/reviews")
    public ResponseEntity getMyReviews( @Positive @RequestParam int page,
                                        @Positive @RequestParam int size,
                                        @Parameter(hidden = true) @AuthenticationPrincipal Member member){
        Page<Review> reviewPage = mypageService.findMyReviewList(page - 1, size, member);
        List<Review> reviews = reviewPage.getContent();

        return new ResponseEntity<>(new MultiResponseDto<>(mapper.reviewToMyReviews(reviews), reviewPage), HttpStatus.OK);
    }
}
