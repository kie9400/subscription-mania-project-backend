package com.springboot.subscription.controller;

import com.springboot.dto.SingleResponseDto;
import com.springboot.member.entity.Member;
import com.springboot.subscription.dto.SubscriptionDto;
import com.springboot.subscription.entity.Subscription;
import com.springboot.subscription.mapper.SubscriptionMapper;
import com.springboot.subscription.service.SubscriptionService;
import com.springboot.utils.UriCreator;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@Tag(name = "구독 API", description = "구독 관련 컨트롤러")
@RestController
@RequestMapping("/subscription")
public class SubscriptionController {
    private static final String SUBSCRIPTION_DEFAULT_URL = "/subscription";
    private final SubscriptionMapper mapper;
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionMapper mapper, SubscriptionService subscriptionService) {
        this.mapper = mapper;
        this.subscriptionService = subscriptionService;
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "구독 등록 완료"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 입니다.(로그인 상태아님)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자 입니다.\"}"))),
            @ApiResponse(responseCode = "409", description = "이미 구독이 되어 있입니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 404, \"message\": \"이미 구독이 되어있습니다.\"}")))
    })
    @PostMapping
    public ResponseEntity postSubs(@Valid @RequestBody SubscriptionDto.Post subsPostDto,
                                   @Parameter(hidden = true) @AuthenticationPrincipal Member member){
        Long platformId = subsPostDto.getPlatformId();
        Long subsPlanId = subsPostDto.getSubsPlanId();

        Subscription subscription = mapper.subsPostDtoToSubs(subsPostDto);
        Subscription createSubs = subscriptionService.createSubs(subscription, member.getMemberId(), platformId, subsPlanId);
        URI location = UriCreator.createUri(SUBSCRIPTION_DEFAULT_URL, createSubs.getSubscriptionId());

        return ResponseEntity.created(location).build();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "구독 수정 완료"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 입니다.(로그인 상태아님)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자 입니다.\"}"))),
            @ApiResponse(responseCode = "404", description = "구독 내역을 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 404, \"message\": \"구독 내역을 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "409", description = "해당 구독은 취소 상태 입니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 404, \"message\": \"해당 구독은 취소 상태 입니다.\"}")))
    })
    @PatchMapping("{subscription-id}")
    public ResponseEntity patchSubs(@PathVariable("subscription-id") long subscriptionId,
                                    @Valid @RequestBody SubscriptionDto.Patch subsPatchDto,
                                    @Parameter(hidden = true) @AuthenticationPrincipal Member member){
        subsPatchDto.setSubscriptionId(subscriptionId);
        Long platformId = subsPatchDto.getPlatformId();
        Long subsPlanId = subsPatchDto.getSubsPlanId();

        Subscription subscription = mapper.subsPatchDtoToSubs(subsPatchDto);
        subscriptionService.updateSubs(subscription, member.getMemberId(), platformId, subsPlanId);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "구독 상세 페이지 조회 완료",
                    content = @Content(
                            mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = SubscriptionDto.Response.class))
                    )),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 입니다.(로그인 상태아님)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자 입니다.\"}"))),
            @ApiResponse(responseCode = "404", description = "구독 내역을 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 404, \"message\": \"구독 내역을 찾을 수 없습니다.\"}")))
    })
    @GetMapping("{subscription-id}")
    public ResponseEntity gatSubs(@PathVariable("subscription-id") long subscriptionId,
                                  @Parameter(hidden = true) @AuthenticationPrincipal Member member){
        Subscription subscription = subscriptionService.findSubscription(subscriptionId, member.getMemberId());
        SubscriptionDto.Response response = mapper.subsToSubsResponseDto(subscription);

        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "구독 삭제 완료"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 입니다.(로그인 상태아님)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자 입니다.\"}"))),
            @ApiResponse(responseCode = "404", description = "구독 내역을 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 404, \"message\": \"구독 내역을 찾을 수 없습니다.\"}"))),
            @ApiResponse(responseCode = "409", description = "해당 구독은 취소 상태 입니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 404, \"message\": \"해당 구독은 취소 상태 입니다.\"}")))
    })
    @DeleteMapping("{subscription-id}")
    public ResponseEntity deleteSubs(@PathVariable("subscription-id") long subscriptionId,
                                    @Parameter(hidden = true) @AuthenticationPrincipal Member member){

        subscriptionService.deleteSubs(subscriptionId, member.getMemberId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
