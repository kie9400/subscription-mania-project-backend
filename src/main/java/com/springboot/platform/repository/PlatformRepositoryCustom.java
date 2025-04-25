package com.springboot.platform.repository;

import com.springboot.member.entity.Member;
import com.springboot.platform.entity.Platform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface PlatformRepositoryCustom {
    //검색 포함 필터링
    Page<Platform> findByCategoryAndKeywordAndRating
            (Long categoryId, String keyword, Integer rating, Pageable pageable, String sort);

    //검색 미포함 필터링
    Page<Platform> findByCategoryAndRating(Long categoryId, Integer rating, Pageable pageable, String sort);

    //평점이 높은 상위 플랫폼 조회
    List<Platform> findTopRatedPlatforms(int limit);

    //나이대 별로 가장 많이 구독한 플랫폼 조회
    List<Platform> findPopularPlatformsByAge(int age, int limit);

    //나이대별 구독 통계 조회
    Map<Integer, Long> countByPlatformSubsByAgeGroup(Long platformId);

    //성별 별 구독 통계 조회
    Map<Member.Gender, Long> countByPlatformSubsByGender(Long platformId);
}
