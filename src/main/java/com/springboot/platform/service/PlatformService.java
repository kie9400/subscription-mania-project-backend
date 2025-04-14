package com.springboot.platform.service;

import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.platform.entity.Platform;
import com.springboot.platform.repository.PlatformRepository;
import nonapi.io.github.classgraph.utils.VersionFinder;
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

    public Platform findPlatform(long platformId){
        Platform findPlatform = findVerifiedPlatform(platformId);

        //활동중인 회원인지 검증이 필요함

        return findPlatform;
    }

    public Platform findVerifiedPlatform(long platformId){
        Optional<Platform> optionalPlatform = platformRepository.findById(platformId);

        Platform platform = optionalPlatform.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.NOT_FOUND));
        return platform;
    }
}
