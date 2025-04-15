package com.springboot.subscription.controller;

import com.springboot.member.entity.Member;
import com.springboot.subscription.dto.SubscriptionDto;
import com.springboot.subscription.entity.Subscription;
import com.springboot.subscription.mapper.SubscriptionMapper;
import com.springboot.subscription.service.SubscriptionService;
import com.springboot.utils.UriCreator;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping
    public ResponseEntity postSubs(@Valid @RequestBody SubscriptionDto.Post postDto,
                                   @Parameter(hidden = true) @AuthenticationPrincipal Member member){
        Long platformId = postDto.getPlatformId();
        Long subsPlanId = postDto.getSubsPlanId();

        Subscription subscription = mapper.subsPostDtoToSubs(postDto);
        Subscription createSubs = subscriptionService.createSubs(subscription, member.getMemberId(), platformId, subsPlanId);
        URI location = UriCreator.createUri(SUBSCRIPTION_DEFAULT_URL, createSubs.getSubscriptionId());

        return ResponseEntity.created(location).build();
    }
}
