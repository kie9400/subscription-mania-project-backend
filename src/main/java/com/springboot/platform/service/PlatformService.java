package com.springboot.platform.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.platform.entity.Platform;
import com.springboot.platform.repository.PlatformRepository;
import nonapi.io.github.classgraph.utils.VersionFinder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    //플랫폼 전체 조회 및 카테고리별 전체 조회
    @Transactional(readOnly = true)
    public Page<Platform> findPlatformsCategory(int page, int size, Long categoryId){
        Pageable pageable = PageRequest.of(page, size, Sort.by("platformId").descending());
        return platformRepository.findAllByCategory(categoryId, pageable);
    }

    //존재하는 플랫폼인지 검증
    public Platform findVerifiedPlatform(long platformId){
        Optional<Platform> optionalPlatform = platformRepository.findById(platformId);

        Platform platform = optionalPlatform.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.NOT_FOUND));
        return platform;
    }
}
