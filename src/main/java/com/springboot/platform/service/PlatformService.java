package com.springboot.platform.service;

import com.springboot.category.entity.Category;
import com.springboot.category.service.CategoryService;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.file.StorageService;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.platform.dto.PlatformDto;
import com.springboot.platform.entity.Platform;
import com.springboot.platform.repository.PlatformRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class PlatformService {
    private final PlatformRepository platformRepository;
    private final MemberService memberService;
    private final CategoryService categoryService;
    private final StorageService storageService;

    public PlatformService(PlatformRepository platformRepository, MemberService memberService, CategoryService categoryService, StorageService storageService) {
        this.platformRepository = platformRepository;
        this.memberService = memberService;
        this.categoryService = categoryService;
        this.storageService = storageService;
    }

    public Platform createPlatform(Member member, Platform platform, long categoryId, MultipartFile image){
        if(!memberService.isAdmin(member.getMemberId())){
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_ADMIN);
        }

        //플랫폼명이 이미 존재하는지 체크
        verifyExistsPlatformName(platform.getPlatformName());

        Category category = categoryService.findVerifiedCategory(categoryId);
        platform.setCategory(category);

        //이미지는 반드시 있어야 한다.
        if (image == null || image.isEmpty()) {
            throw new BusinessLogicException(ExceptionCode.IMAGE_NOT_FOUND);
        }

        String imageUrl = uploadImage(image);
        platform.setPlatformImage(imageUrl);

        return platformRepository.save(platform);
    }

    public void deletePlatform(Member member, Long platformId){
        if(!memberService.isAdmin(member.getMemberId())){
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_ADMIN);
        }
        Platform findPlatform = findVerifiedPlatform(platformId);

        platformRepository.delete(findPlatform);
    }


    @Transactional(readOnly = true)
    public Platform findPlatform(long platformId) {
        Platform findPlatform = findVerifiedPlatform(platformId);

        return findPlatform;
    }

    //플랫폼 전체 조회 (카테고리, 별점, 검색 포함)
    @Transactional(readOnly = true)
    public Page<Platform> findSearchPlatforms(int page, int size, Long categoryId, String keyword, Integer rating, String sort) {
        Pageable pageable = PageRequest.of(page, size);

        // 검색어가 아예 없으면 전체 조회
        if (keyword == null) {
            return platformRepository.findByCategoryAndRating(categoryId, rating, pageable, sort);
        }

        // 검색어가 있지만 공백뿐이면 예외 처리
        if (keyword.isBlank()) {
            throw new BusinessLogicException(ExceptionCode.SEARCH_NOT_BLANK);
        }

        return platformRepository.findByCategoryAndKeywordAndRating(categoryId, keyword, rating, pageable, sort);
    }

    @Transactional(readOnly = true)
    public Page<Platform> adminGetPlatforms(int page, int size, long memberId) {
        if(!memberService.isAdmin(memberId)){
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_ADMIN);
        }
        Pageable pageable = PageRequest.of(page, size);

        return platformRepository.findByPlatforms(pageable);
    }

    //존재하는 플랫폼인지 검증
    public Platform findVerifiedPlatform(long platformId) {
        Optional<Platform> optionalPlatform = platformRepository.findById(platformId);

        Platform platform = optionalPlatform.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.NOT_FOUND));
        return platform;
    }

    //평점이 높은 상위 10개 플랫폼 조회하는 메서드
    public List<Platform> findTopRatedPlatforms() {
        return platformRepository.findTopRatedPlatforms(10);
    }

    //나이대 별로 가장 많이 구독한 10개 플랫폼 조회하는 메서드
    public List<Platform> findPopularPlatformsByAge(Member member) {
        Member findMember = memberService.findVerifiedMember(member.getMemberId());
        return platformRepository.findPopularPlatformsByAge(findMember.getAge(), 10);
    }

    //플랫폼 별 통계 조회 (나이대별 구독, 성별별 구독)
    public PlatformDto.PlatformStatisticsResponse getStatistics(Long platformId) {
        Map<Member.Gender, Long> genderStats = platformRepository.countByPlatformSubsByGender(platformId);
        Map<Integer, Long> ageStats = platformRepository.countByPlatformSubsByAgeGroup(platformId);

        return new PlatformDto.PlatformStatisticsResponse(genderStats, ageStats);
    }

    //플랫폼명 중복 검증 메서드
    public void verifyExistsPlatformName(String platformName) {
        Optional<Platform> optionalPlatform = platformRepository.findByPlatformName(platformName);

        if (optionalPlatform.isPresent()) {
            throw new BusinessLogicException(ExceptionCode.ALREADY_EXISTS);
        }
    }

    //이미지 등록
    public String uploadImage(MultipartFile multipartFile){
        if (multipartFile != null && !multipartFile.isEmpty()){
            String fileName = multipartFile.getOriginalFilename();
            //이미지명에서 확장자 제거
            String fileNameWithoutExt = fileName.replaceFirst("\\.[^.]+$", "");

            // 프로필 이미지 덮어쓰기 되도록 구현
            String pathWithoutExt = "platform/" + fileNameWithoutExt;
            return storageService.store(multipartFile, pathWithoutExt);
        }
        return null;
    }
}
