package com.springboot.home.controller;

import com.springboot.category.dto.CategoryDto;
import com.springboot.category.entity.Category;
import com.springboot.category.mapper.CategoryMapper;
import com.springboot.category.service.CategoryService;
import com.springboot.dto.SingleResponseDto;
import com.springboot.home.dto.MainDto;
import com.springboot.home.mapper.MainMapper;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "메인페이지 API", description = "메인페이지 관련 API")
@RestController
@RequiredArgsConstructor
public class MainController {
    private final MemberService memberService;
    private final CategoryService categoryService;
    private final PlatformService platformService;
    private final CategoryMapper categoryMapper;
    private final PlatformMapper platformMapper;
    private final MainMapper mapper;

    //메인 페이지

    @Operation(summary = "메인 페이지 조회", description = "카테고리 목록과 사용자 맞춤/비회원용 추천 플랫폼 목록을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            value = "{ \"data\": { \"categories\": [ { \"categoryId\": 1, \"categoryName\": \"문화\", \"categoryImage\": \"/images/category/culture.png\" } ], \"platforms\": [ { \"platformId\": 1, \"platformName\": \"넷플릭스\", \"platformImage\": \"/images/platform/netflix.png\", \"ratingAvg\": 4.5 } ] } }"
            ))),
            @ApiResponse(responseCode = "200", description = "비회원인 경우에도 기본 추천 플랫폼이 제공됩니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(
                            value = "{ \"data\": { \"categories\": [...], \"platforms\": [...] } }")))
    })
    @GetMapping("/main")
    public ResponseEntity getMainPage(@Parameter(hidden = true) @AuthenticationPrincipal Member member){
        List<Category> categories = categoryService.findCategories();
        List<Platform> platforms;

        if(member != null){
            platforms = platformService.findPopularPlatformsByAge(member);
        }else {
            platforms = platformService.findTopRatedPlatforms();
        }

        List<CategoryDto.Response> categoryResponse = categoryMapper.categoryToCategoryResponseDtos(categories);
        List<PlatformDto.AllResponse> platformResponse = platformMapper.toAllResponseList(platforms);

        MainDto mainDto = mapper.responseDtoToMainDto(categoryResponse, platformResponse);
        return new ResponseEntity<>(new SingleResponseDto<>(mainDto), HttpStatus.OK);
    }
}
