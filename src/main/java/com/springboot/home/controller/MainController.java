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
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
