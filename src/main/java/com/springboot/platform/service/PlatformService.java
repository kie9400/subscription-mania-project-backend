package com.springboot.platform.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.entity.Member;
import com.springboot.member.service.MemberService;
import com.springboot.platform.dto.PlatformDto;
import com.springboot.platform.entity.Platform;
import com.springboot.platform.repository.PlatformRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class PlatformService {
    private final PlatformRepository platformRepository;
    private final MemberService memberService;

    public PlatformService(PlatformRepository platformRepository, MemberService memberService) {
        this.platformRepository = platformRepository;
        this.memberService = memberService;
    }

    @Transactional(readOnly = true)
    public Platform findPlatform(long platformId) {
        Platform findPlatform = findVerifiedPlatform(platformId);

        //활동중인 회원인지 검증이 필요함
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
}
