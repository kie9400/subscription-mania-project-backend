package com.springboot.platform.controller;

import com.springboot.dto.SingleResponseDto;
import com.springboot.member.entity.Member;
import com.springboot.platform.dto.PlatformDto;
import com.springboot.platform.entity.Platform;
import com.springboot.platform.mapper.PlatformMapper;
import com.springboot.platform.service.PlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;

@Tag(name = "플랫폼 API", description = "플랫폼 관련 API")
@RestController
@RequestMapping("/platforms")
@Validated
public class PlatformController {
    private final PlatformMapper mapper;
    private final PlatformService platformService;

    public PlatformController(PlatformMapper mapper, PlatformService platformService) {
        this.mapper = mapper;
        this.platformService = platformService;
    }

    @Operation(summary = "플랫폼 단일 조회", description = "플롯폼 상세 페이지를 조회 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "플랫폼을 찾을 수 없습니다.")
    })
    @GetMapping("{platform-id}")
    public ResponseEntity getGroup(@PathVariable("platform-id") @Positive long platformId) {
        Platform platform = platformService.findPlatform(platformId);
        PlatformDto.Response response = mapper.platformToPlatformResponse(platform);

        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }

}
