package com.springboot.platform.controller;

import com.springboot.dto.MultiResponseDto;
import com.springboot.dto.SingleResponseDto;
import com.springboot.platform.dto.PlatformDto;
import com.springboot.platform.entity.Platform;
import com.springboot.platform.mapper.PlatformMapper;
import com.springboot.platform.service.PlatformService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "플랫폼 단일 조회", description = "플랫폼 상세 페이지를 조회 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 입니다.(로그인 상태아님)",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"error\": \"Unauthorized\", \"message\": \"인증되지 않은 사용자 입니다.\"}"))),
            @ApiResponse(responseCode = "404", description = "플랫폼을 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = "{\"status\": 404, \"message\": \"플랫폼을 찾을 수 없습니다.\"}")))
    })
    @GetMapping("{platform-id}")
    public ResponseEntity getGroup(@PathVariable("platform-id") @Positive long platformId) {
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
}
