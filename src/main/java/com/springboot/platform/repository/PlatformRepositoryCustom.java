package com.springboot.platform.repository;

import com.springboot.platform.entity.Platform;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PlatformRepositoryCustom {
    //검색 포함 필터링
    Page<Platform> findByCategoryAndKeywordAndRating
            (Long categoryId, String keyword, Integer rating, Pageable pageable, String sort);

    //검색 미포함 필터링
    Page<Platform> findByCategoryAndRating(Long categoryId, Integer rating, Pageable pageable, String sort);
}
