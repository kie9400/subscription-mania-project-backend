package com.springboot.platform.controller;

import com.springboot.dto.MultiResponseDto;
import com.springboot.dto.SingleResponseDto;
import com.springboot.member.entity.Member;
import com.springboot.platform.dto.PlatformDto;
import com.springboot.platform.entity.Platform;
import com.springboot.platform.mapper.PlatformMapper;
import com.springboot.platform.service.PlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

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

    @Operation(summary = "플랫폼 등록", description = "플랫폼 상세 페이지를 조회 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "플랫폼 등록 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 입니다.(로그인 상태아님)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자 입니다.\"}"))),
            @ApiResponse(responseCode = "403", description = "관리자 계정이 아닙니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Forbidden\", \"message\": \"권한이 없는 사용자 입니다.\"}"))),
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity postPlatform(@Valid @RequestPart("data") PlatformDto.Post postDto,
                                       @RequestPart(required = false) MultipartFile image,
                                       @Parameter(hidden = true) @AuthenticationPrincipal Member member){
        Long categoryId = postDto.getCategoryId();
        platformService.createPlatform(member, mapper.platformPostToPlatform(postDto), categoryId, image);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "플랫폼 단일 조회", description = "플랫폼 상세 페이지를 조회 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            value = "{ \"data\": { \"platformId\": 1, \"platformName\": \"넷플릭스\", \"platformImage\": \"/images/platform/netflix.png\", \"platformDescription\": \"다양한 영화와 드라마를 제공하는 OTT 서비스\", \"categoryName\": \"문화\", \"serviceAt\": \"2016-01-07\", \"ratingAvg\": 0, \"reviewCount\": 0, \"plans\": [ { \"subsPlanId\": 1, \"planName\": \"광고형 스탠다드\", \"price\": 5500 }, { \"subsPlanId\": 2, \"planName\": \"스탠다드\", \"price\": 13500 }, { \"subsPlanId\": 3, \"planName\": \"프리미엄\", \"price\": 17000 } ] } }"))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 입니다.(로그인 상태아님)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자 입니다.\"}"))),
            @ApiResponse(responseCode = "404", description = "플랫폼을 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 404, \"message\": \"플랫폼을 찾을 수 없습니다.\"}")))
    })
    @GetMapping("{platform-id}")
    public ResponseEntity getPlatform(@PathVariable("platform-id") @Positive long platformId) {
        Platform platform = platformService.findPlatform(platformId);
        PlatformDto.Response response = mapper.platformToPlatformResponse(platform);

        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }

    @Operation(summary = "플랫폼 전체 조회", description = "플롯폼 전체를 조회합니다. 카테고리 선택시 카테고리별로 전체 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 입니다.(로그인 상태아님)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자 입니다.\"}"))),
            @ApiResponse(responseCode = "400", description = "검색어가 공백입니다. 입력해야 합니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 400, \"message\": \"검색창이 비어 있습니다.\"}")))
    })
    @GetMapping
    public ResponseEntity getPlatforms(@RequestParam(required = false) Long categoryId,
                                       @RequestParam(required = false) String keyword,
                                       @RequestParam(required = false) Integer rating,
                                       @RequestParam(defaultValue = "platformId") String sort,
                                       @RequestParam int page,
                                       @RequestParam int size) {
        Page<Platform> platformPage = platformService.findSearchPlatforms(page - 1, size, categoryId, keyword, rating, sort);
        List<Platform> platforms = platformPage.getContent();
        return new ResponseEntity<>(
                new MultiResponseDto<>(mapper.toAllResponseList(platforms), platformPage), HttpStatus.OK);
    }

    @Operation(summary = "플랫폼별 구독 통계 조회", description = "해당 플롯폼의 나이대별, 성별 별 구독 통계를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject
                            (value = "{ \"data\": { \"genderStats\": { \"MALE\": 15, \"FEMALE\": 12 }, \"ageStats\": { \"10\": 1, \"20\": 6, \"30\": 12, \"40\": 5 } } }"))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 입니다.(로그인 상태아님)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자 입니다.\"}")))
    })
    @GetMapping("{platform-id}/statistics")
    public ResponseEntity getStatistics(@PathVariable("platform-id") @Positive long platformId){
        PlatformDto.PlatformStatisticsResponse response = platformService.getStatistics(platformId);

        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }
}
