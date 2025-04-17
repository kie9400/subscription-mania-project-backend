package com.springboot.platform.repository;

import com.springboot.platform.entity.Platform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PlatformRepositoryCustom {
    //검색 포함 필터링
    Page<Platform> findByCategoryAndKeywordAndRating
            (Long categoryId, String keyword, Integer rating, Pageable pageable, String sort);

    //검색 미포함 필터링
    Page<Platform> findByCategoryAndRating(Long categoryId, Integer rating, Pageable pageable, String sort);

    //평점이 높은 상위 8개 플랫폼 조회
    List<Platform> findTopRatedPlatforms(int limit);

    //나이대 별로 가장 많이 구독한 8개 플랫폼 조회
    List<Platform> findPopularPlatformsByAge(int age, int limit);
}
