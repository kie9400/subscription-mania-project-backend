package com.springboot.platform.repository;

import com.springboot.platform.entity.Platform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.parameters.P;

import java.util.Optional;

public interface PlatformRepository extends JpaRepository<Platform, Long>, PlatformRepositoryCustom {
    Optional<Platform> findByPlatformName(String name);
}
