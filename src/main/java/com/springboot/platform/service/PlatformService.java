package com.springboot.platform.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.platform.entity.Platform;
import com.springboot.platform.repository.PlatformRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class PlatformService {
    private final PlatformRepository platformRepository;

    public PlatformService(PlatformRepository platformRepository) {
        this.platformRepository = platformRepository;
    }

    @Transactional(readOnly = true)
    public Platform findPlatform(long platformId){
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
        if (keyword.isBlank() ) {
            throw new BusinessLogicException(ExceptionCode.SEARCH_NOT_BLANK);
        }

        return platformRepository.findByCategoryAndKeywordAndRating(categoryId, keyword, rating, pageable, sort);
    }

    //존재하는 플랫폼인지 검증
    public Platform findVerifiedPlatform(long platformId){
        Optional<Platform> optionalPlatform = platformRepository.findById(platformId);

        Platform platform = optionalPlatform.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.NOT_FOUND));
        return platform;
    }
}
